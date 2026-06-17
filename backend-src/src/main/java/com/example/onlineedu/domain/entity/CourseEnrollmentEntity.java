package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 课程报名实体类
 *
 * @author feng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_enrollment")
public class CourseEnrollmentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 课程ID
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 报名时间
     */
    @TableField("enroll_time")
    private LocalDateTime enrollTime;

    /**
     * 最后学习时间
     */
    @TableField("last_learn_time")
    private LocalDateTime lastLearnTime;
}
