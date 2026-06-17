package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程章节树形结构 VO
 *
 * @author feng
 */
@Data
@ApiModel("课程章节树形结构")
public class CourseChapterTreeVO {

    /**
     * 章节ID
     */
    @ApiModelProperty("章节ID")
    private Long id;

    /**
     * 课程ID
     */
    @ApiModelProperty("课程ID")
    private Long courseId;

    /**
     * 章节名称
     */
    @ApiModelProperty("章节名称")
    private String chapterName;

    /**
     * 父章节ID
     */
    @ApiModelProperty("父章节ID")
    private Long parentId;

    /**
     * 排序序号
     */
    @ApiModelProperty("排序序号")
    private Integer sortOrder;

    /**
     * 视频URL
     */
    @ApiModelProperty("视频URL")
    private String videoUrl;

    /**
     * 视频时长（秒）
     */
    @ApiModelProperty("视频时长")
    private Integer videoDuration;

    /**
     * 是否免费试看
     */
    @ApiModelProperty("是否免费试看")
    private Integer isFree;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 子章节列表（小节）
     */
    @ApiModelProperty("子章节列表")
    private List<CourseChapterTreeVO> children;

    /**
     * 最后播放位置(秒)
     */
    @ApiModelProperty("最后播放位置")
    private Integer lastPosition;

    /**
     * 历史最高播放位置（秒）
     */
    @ApiModelProperty("历史最高播放位置")
    private Integer maxPosition;

    /**
     * 是否已完成(0-未完成 1-已完成)
     */
    @ApiModelProperty("是否已完成")
    private Integer isCompleted;

    /**
     * 学习进度百分比(0-100)
     */
    @ApiModelProperty("学习进度百分比")
    private Integer progressPercent;
}
