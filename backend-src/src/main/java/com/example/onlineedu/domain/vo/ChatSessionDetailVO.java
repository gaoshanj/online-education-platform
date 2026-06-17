package com.example.onlineedu.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天会话详情视图对象
 *
 * @author feng
 */
@Data
public class ChatSessionDetailVO {

    /**
     * 会话ID
     */
    private Long id;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 消息列表（按创建时间升序）
     */
    private List<ChatMessageVO> messages;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
