package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 回答/评论实体类（一级为回答，二级为评论）
 *
 * @author feng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("answer")
public class AnswerEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 问题ID
     */
    @TableField("question_id")
    private Long questionId;

    /**
     * 回复的一级回答ID（0 表示该记录本身是回答；>0 表示是某回答下的评论）
     */
    @TableField("answer_id")
    private Long answerId;

    /**
     * 回答/评论用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 内容
     */
    @TableField("content")
    private String content;

    /**
     * 是否为讲师回答：0-否，1-是
     */
    @TableField("is_teacher")
    private Integer isTeacher;

    /**
     * 回复的目标回复ID（评论中回复某条评论时使用，0 表示直接回复回答）
     */
    @TableField("target_reply_id")
    private Long targetReplyId;

    /**
     * 回复的目标用户ID
     */
    @TableField("target_user_id")
    private Long targetUserId;

    /**
     * 评论数量（仅一级回答有效，记录该回答下的二级评论数）
     */
    @TableField("reply_times")
    private Integer replyTimes;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;
}
