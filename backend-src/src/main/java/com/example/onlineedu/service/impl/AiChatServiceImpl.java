package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.ChatMessageDTO;
import com.example.onlineedu.domain.entity.AiChatMessageEntity;
import com.example.onlineedu.domain.entity.AiChatSessionEntity;
import com.example.onlineedu.domain.vo.ChatMessageVO;
import com.example.onlineedu.domain.vo.ChatResponseVO;
import com.example.onlineedu.domain.vo.ChatSessionDetailVO;
import com.example.onlineedu.domain.vo.ChatSessionVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.AiChatMessageMapper;
import com.example.onlineedu.mapper.AiChatSessionMapper;
import com.example.onlineedu.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI聊天服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiChatSessionMapper sessionMapper;

    @Autowired
    private AiChatMessageMapper messageMapper;

    @Autowired
    private AiChatAsyncService aiChatAsyncService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMessage(Long userId, ChatMessageDTO dto) {
        Long sessionId = dto.getSessionId();

        // 1. sessionId 为空则创建新会话
        if (sessionId == null) {
            AiChatSessionEntity session = createNewSession(userId, dto.getContent());
            sessionId = session.getId();
        } else {
            // 验证会话归属
            AiChatSessionEntity session = sessionMapper.selectById(sessionId);
            if (session == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
            }
            if (!session.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.NO_AUTH, "无权访问此会话");
            }
        }

        // 2. 保存用户消息
        AiChatMessageEntity userMessage = new AiChatMessageEntity();
        userMessage.setSessionId(sessionId);
        userMessage.setRole("user");
        userMessage.setContent(dto.getContent());
        userMessage.setCreateTime(LocalDateTime.now());
        messageMapper.insert(userMessage);

        // 3. 触发异步 AI 回复
        aiChatAsyncService.asyncAiReply(sessionId, dto.getContent());

        // 4. 立即返回 sessionId，不等待 AI
        return sessionId;
    }

    @Override
    public ChatResponseVO getLatestReply(Long sessionId) {
        // 查询该会话最新一条消息
        LambdaQueryWrapper<AiChatMessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessageEntity::getSessionId, sessionId)
                .orderByDesc(AiChatMessageEntity::getCreateTime)
                .last("LIMIT 1");

        AiChatMessageEntity latestMessage = messageMapper.selectOne(wrapper);

        // 如果最新消息是 assistant 的回复，则返回
        if (latestMessage != null && "assistant".equals(latestMessage.getRole())) {
            ChatResponseVO response = new ChatResponseVO();
            response.setSessionId(sessionId);
            response.setAssistantMessage(convertToVO(latestMessage));
            return response;
        }

        // AI 尚未回复
        return null;
    }

    @Override
    public Page<ChatSessionVO> listSessions(Long userId, Integer page, Integer size) {
        Page<AiChatSessionEntity> entityPage = new Page<>(page, size);

        LambdaQueryWrapper<AiChatSessionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatSessionEntity::getUserId, userId)
                .orderByDesc(AiChatSessionEntity::getUpdateTime);

        sessionMapper.selectPage(entityPage, wrapper);

        // 转换为VO
        Page<ChatSessionVO> voPage = new Page<>(page, size, entityPage.getTotal());
        List<ChatSessionVO> voList = entityPage.getRecords().stream()
                .map(this::convertSessionToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public ChatSessionDetailVO getSessionDetail(Long userId, Long sessionId) {
        // 验证会话归属
        AiChatSessionEntity session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
        }
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权访问此会话");
        }

        // 查询所有消息
        LambdaQueryWrapper<AiChatMessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessageEntity::getSessionId, sessionId)
                .orderByAsc(AiChatMessageEntity::getCreateTime);

        List<AiChatMessageEntity> messages = messageMapper.selectList(wrapper);

        // 构造详情VO
        ChatSessionDetailVO detailVO = new ChatSessionDetailVO();
        detailVO.setId(session.getId());
        detailVO.setTitle(session.getTitle());
        detailVO.setCreateTime(session.getCreateTime());
        detailVO.setMessages(messages.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(Long userId, Long sessionId) {
        // 验证会话归属
        AiChatSessionEntity session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
        }
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权删除此会话");
        }

        // 逻辑删除会话
        sessionMapper.deleteById(sessionId);

        // 物理删除该会话的所有消息
        LambdaQueryWrapper<AiChatMessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessageEntity::getSessionId, sessionId);
        messageMapper.delete(wrapper);
    }

    /**
     * 创建新会话（使用消息前20字作为标题，避免额外 AI 调用）
     */
    private AiChatSessionEntity createNewSession(Long userId, String firstMessage) {
        String title = firstMessage.substring(0, Math.min(20, firstMessage.length()));

        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setUserId(userId);
        session.setTitle(title);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        sessionMapper.insert(session);

        return session;
    }

    /**
     * 转换消息实体为VO
     */
    private ChatMessageVO convertToVO(AiChatMessageEntity entity) {
        ChatMessageVO vo = new ChatMessageVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 转换会话实体为VO
     */
    private ChatSessionVO convertSessionToVO(AiChatSessionEntity entity) {
        ChatSessionVO vo = new ChatSessionVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}

