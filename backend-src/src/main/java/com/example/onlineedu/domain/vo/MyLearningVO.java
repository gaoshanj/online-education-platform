package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我的学习课程 VO
 *
 * @author feng
 */
@Data
@ApiModel("我的学习课程响应")
public class MyLearningVO {

    /**
     * 课程ID
     */
    @ApiModelProperty("课程ID")
    private Long courseId;

    /**
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     * 课程封面图
     */
    @ApiModelProperty("课程封面图")
    private String coverImage;

    /**
     * 讲师名称
     */
    @ApiModelProperty("讲师名称")
    private String teacherName;

    /**
     * 报名时间
     */
    @ApiModelProperty("报名时间")
    private LocalDateTime enrollTime;

    /**
     * 最后学习时间
     */
    @ApiModelProperty("最后学习时间")
    private LocalDateTime lastLearnTime;

    /**
     * 学习进度百分比（0-100）
     */
    @ApiModelProperty("学习进度百分比")
    private Integer learningProgress;

    /**
     * 课程总章节数
     */
    @ApiModelProperty("课程总章节数")
    private Integer totalChapters;

    /**
     * 已完成章节数
     */
    @ApiModelProperty("已完成章节数")
    private Integer completedChapters;

    /**
     * 最近学习的章节ID
     */
    @ApiModelProperty("最近学习的章节ID")
    private Long recentChapterId;

    /**
     * 最近学习的章节名称
     */
    @ApiModelProperty("最近学习的章节名称")
    private String recentChapterName;
}
