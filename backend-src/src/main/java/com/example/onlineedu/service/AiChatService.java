package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.dto.ChatMessageDTO;
import com.example.onlineedu.domain.vo.ChatResponseVO;
import com.example.onlineedu.domain.vo.ChatSessionDetailVO;
import com.example.onlineedu.domain.vo.ChatSessionVO;

/**
 * AI聊天服务接口
 *
 * @author feng
 */
public interface AiChatService {

    /**
     * 发送消息（异步调用AI），立即返回 sessionId
     *
     * @param userId 用户ID
     * @param dto    消息DTO
     * @return sessionId
     */
    Long sendMessage(Long userId, ChatMessageDTO dto);

    /**
     * 轮询查询最新AI回复
     *
     * @param sessionId 会话ID
     * @return 若AI已回复则返回 ChatResponseVO，否则返回 null
     */
    ChatResponseVO getLatestReply(Long sessionId);

    /**
     * 分页查询用户的会话列表
     *
     * @param userId 用户ID
     * @param page   当前页
     * @param size   每页大小
     * @return 会话列表
     */
    Page<ChatSessionVO> listSessions(Long userId, Integer page, Integer size);

    /**
     * 查询会话详情（包含所有消息）
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 会话详情
     */
    ChatSessionDetailVO getSessionDetail(Long userId, Long sessionId);

    /**
     * 删除会话（级联删除消息）
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     */
    void deleteSession(Long userId, Long sessionId);
}

