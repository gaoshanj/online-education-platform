package com.example.onlineedu.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息视图对象
 *
 * @author feng
 */
@Data
public class ChatMessageVO {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 角色：user/assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
