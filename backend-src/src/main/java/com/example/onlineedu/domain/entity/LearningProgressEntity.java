package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学习进度实体类
 *
 * @author feng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("learning_progress")
public class LearningProgressEntity extends BaseEntity {

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
     * 章节ID
     */
    @TableField("chapter_id")
    private Long chapterId;

    /**
     * 最后播放位置（秒）
     */
    @TableField("last_position")
    private Integer lastPosition;

    /**
     * 历史最高播放位置（秒）
     */
    @TableField("max_position")
    private Integer maxPosition;

    /**
     * 已学习时长（秒）
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 是否完成：0-未完成，1-已完成
     */
    @TableField("is_completed")
    private Integer isCompleted;
}
