package com.example.onlineedu.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 回答/评论提交DTO
 * - answerId = 0 或 null：提交一级回答
 * - answerId > 0：提交二级评论，answerId 为所属一级回答的 ID
 *
 * @author feng
 */
@Data
public class AnswerDTO {

    /**
     * 问题ID
     */
    @NotNull(message = "问题ID不能为空")
    private Long questionId;

    /**
     * 所属一级回答ID（0 或 null 表示这是一个回答；> 0 表示这是一条评论）
     */
    private Long answerId;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 被回复的目标评论ID（评论中回复某条评论时使用，可选）
     */
    private Long targetReplyId;

    /**
     * 被回复的目标用户ID（可选）
     */
    private Long targetUserId;
}
