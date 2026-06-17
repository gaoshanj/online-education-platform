package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 问答实体类
 *
 * @author feng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("question")
public class QuestionEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 提问用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 问题标题
     */
    @TableField("title")
    private String title;

    /**
     * 问题内容
     */
    @TableField("content")
    private String content;

    /**
     * 最新回答ID（用于列表展示最新回答预览）
     */
    @TableField("latest_answer_id")
    private Long latestAnswerId;

    /**
     * 回答数量（不包括评论）
     */
    @TableField("answer_count")
    private Integer answerCount;

    /**
     * 是否有讲师回答：0-否，1-是
     */
    @TableField("has_teacher_answer")
    private Integer hasTeacherAnswer;
}
