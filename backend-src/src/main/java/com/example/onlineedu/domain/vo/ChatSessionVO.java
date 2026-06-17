package com.example.onlineedu.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话视图对象（列表展示）
 *
 * @author feng
 */
@Data
public class ChatSessionVO {

    /**
     * 会话ID
     */
    private Long id;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
