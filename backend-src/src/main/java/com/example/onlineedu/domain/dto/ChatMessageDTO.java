package com.example.onlineedu.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 聊天消息请求DTO
 *
 * @author feng
 */
@Data
public class ChatMessageDTO {

    /**
     * 会话ID（可选，首次提问时不传）
     */
    private Long sessionId;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;
}
