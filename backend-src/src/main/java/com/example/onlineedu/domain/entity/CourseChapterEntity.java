package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程章节实体类
 *
 * @author feng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_chapter")
public class CourseChapterEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 章节名称
     */
    @TableField("chapter_name")
    private String chapterName;

    /**
     * 父章节ID（0表示一级章节）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 视频URL
     */
    @TableField("video_url")
    private String videoUrl;

    /**
     * 视频时长（秒）
     */
    @TableField("video_duration")
    private Integer videoDuration;

}
