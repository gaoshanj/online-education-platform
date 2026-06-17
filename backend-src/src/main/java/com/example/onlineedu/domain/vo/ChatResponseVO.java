package com.example.onlineedu.domain.vo;

import lombok.Data;

/**
 * 聊天响应视图对象
 *
 * @author feng
 */
@Data
public class ChatResponseVO {

    /**
     * 会话ID（新建或现有）
     */
    private Long sessionId;

    /**
     * 用户消息
     */
    private ChatMessageVO userMessage;

    /**
     * AI回复消息
     */
    private ChatMessageVO assistantMessage;
}
