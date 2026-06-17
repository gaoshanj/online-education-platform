package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 章节学习进度 VO
 *
 * @author feng
 */
@Data
@ApiModel("章节学习进度响应")
public class ChapterProgressVO {

    /**
     * 章节ID
     */
    @ApiModelProperty("章节ID")
    private Long chapterId;

    /**
     * 章节名称
     */
    @ApiModelProperty("章节名称")
    private String chapterName;

    /**
     * 最后播放位置（秒）
     */
    @ApiModelProperty("最后播放位置（秒）")
    private Integer lastPosition;

    /**
     * 历史最高播放位置（秒）
     */
    @ApiModelProperty("历史最高播放位置（秒）")
    private Integer maxPosition;

    /**
     * 已学习时长（秒）
     */
    @ApiModelProperty("已学习时长（秒）")
    private Integer duration;

    /**
     * 视频总时长（秒）
     */
    @ApiModelProperty("视频总时长（秒）")
    private Integer videoDuration;

    /**
     * 是否完成：0-未完成，1-已完成
     */
    @ApiModelProperty("是否完成")
    private Integer isCompleted;
}
