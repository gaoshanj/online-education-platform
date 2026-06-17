package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程评价实体类
 *
 * @author feng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_review")
public class CourseReviewEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 评价用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 评分 1-5
     */
    @TableField("rating")
    private Integer rating;

    /**
     * 评价内容
     */
    @TableField("content")
    private String content;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;
}
