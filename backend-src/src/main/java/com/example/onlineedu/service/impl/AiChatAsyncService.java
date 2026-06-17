package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlineedu.domain.entity.AiChatMessageEntity;
import com.example.onlineedu.domain.entity.AiChatSessionEntity;
import com.example.onlineedu.mapper.AiChatMessageMapper;
import com.example.onlineedu.mapper.AiChatSessionMapper;
import com.example.onlineedu.service.QwenClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI聊天异步服务
 * 独立 Bean，保证 @Async 通过 Spring AOP 代理生效
 *
 * @author feng
 */
@Slf4j
@Service
public class AiChatAsyncService {

    @Autowired
    private AiChatMessageMapper messageMapper;

    @Autowired
    private AiChatSessionMapper sessionMapper;

    @Autowired
    private QwenClient qwenClient;

    private static final String SYSTEM_PROMPT = "你是一个专业的在线教育知识问答助手。你的任务是用简洁、清晰、易懂的方式回答用户的各类知识问题。\n" +
            "请注意：\n" +
            "1. 回答要准确、有条理\n" +
            "2. 对于复杂问题，可以分点说明\n" +
            "3. 如果不确定答案，请诚实告知\n" +
            "4. 保持友好、耐心的语气";

    /**
     * 异步调用 AI 并保存回复
     *
     * @param sessionId 会话ID
     * @param question  用户问题
     */
    @Async
    public void asyncAiReply(Long sessionId, String question) {
        try {
            // 1. 查询最近 10 条消息作为上下文
            List<AiChatMessageEntity> recentMessages = getRecentMessages(sessionId, 10);

            // 2. 构建上下文消息列表
            List<Map<String, String>> messages = buildContextMessages(recentMessages, question);

            // 3. 调用千问 API
            String aiReply = qwenClient.chat(messages);

            // 4. 保存 AI 回复
            AiChatMessageEntity assistantMessage = new AiChatMessageEntity();
            assistantMessage.setSessionId(sessionId);
            assistantMessage.setRole("assistant");
            assistantMessage.setContent(aiReply);
            assistantMessage.setCreateTime(LocalDateTime.now());
            messageMapper.insert(assistantMessage);

            // 5. 更新会话更新时间
            AiChatSessionEntity session = new AiChatSessionEntity();
            session.setId(sessionId);
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.updateById(session);

            log.info("AI 异步回复完成，sessionId={}", sessionId);
        } catch (Exception e) {
            log.error("AI 异步回复失败，sessionId={}，原因：{}", sessionId, e.getMessage(), e);
        }
    }

    /**
     * 获取最近 N 条消息（按时间升序）
     */
    private List<AiChatMessageEntity> getRecentMessages(Long sessionId, int limit) {
        LambdaQueryWrapper<AiChatMessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessageEntity::getSessionId, sessionId)
                .orderByDesc(AiChatMessageEntity::getCreateTime)
                .last("LIMIT " + limit);

        List<AiChatMessageEntity> messages = messageMapper.selectList(wrapper);
        // 反转为时间升序
        Collections.reverse(messages);
        return messages;
    }

    /**
     * 构建包含系统提示和历史消息的上下文
     */
    private List<Map<String, String>> buildContextMessages(List<AiChatMessageEntity> recentMessages, String currentQuestion) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 系统提示
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", SYSTEM_PROMPT);
        messages.add(systemMessage);

        // 历史消息
        for (AiChatMessageEntity msg : recentMessages) {
            Map<String, String> message = new HashMap<>();
            message.put("role", msg.getRole());
            message.put("content", msg.getContent());
            messages.add(message);
        }

        // 当前问题
        Map<String, String> currentMessage = new HashMap<>();
        currentMessage.put("role", "user");
        currentMessage.put("content", currentQuestion);
        messages.add(currentMessage);

        return messages;
    }
}
