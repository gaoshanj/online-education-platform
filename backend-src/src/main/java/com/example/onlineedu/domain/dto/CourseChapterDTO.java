package com.example.onlineedu.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 课程章节 DTO
 *
 * @author feng
 */
@Data
@ApiModel("课程章节请求")
public class CourseChapterDTO {

    /**
     * 章节ID（更新时需要）
     */
    @ApiModelProperty("章节ID")
    private Long id;

    /**
     * 课程ID
     */
    @ApiModelProperty(value = "课程ID", required = true, example = "1")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 章节名称
     */
    @ApiModelProperty(value = "章节名称", required = true, example = "第一章：Java基础")
    @NotBlank(message = "章节名称不能为空")
    private String chapterName;

    /**
     * 父章节ID（0表示一级章节/章，非0表示二级章节/小节）
     */
    @ApiModelProperty(value = "父章节ID", example = "0")
    private Long parentId = 0L;

    /**
     * 排序序号
     */
    @ApiModelProperty(value = "排序序号", example = "1")
    private Integer sortOrder = 0;

    /**
     * 视频URL（仅小节有视频）
     */
    @ApiModelProperty(value = "视频URL")
    private String videoUrl;

    /**
     * 视频时长（秒）
     */
    @ApiModelProperty(value = "视频时长", example = "600")
    private Integer videoDuration = 0;

    /**
     * 是否免费试看
     */
    @ApiModelProperty(value = "是否免费试看", example = "0")
    private Integer isFree = 0;
}
