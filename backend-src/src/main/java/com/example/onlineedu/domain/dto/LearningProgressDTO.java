package com.example.onlineedu.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 学习进度 DTO
 *
 * @author feng
 */
@Data
@ApiModel("学习进度请求")
public class LearningProgressDTO {

    /**
     * 课程ID
     */
    @ApiModelProperty(value = "课程ID", required = true)
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 章节ID
     */
    @ApiModelProperty(value = "章节ID", required = true)
    @NotNull(message = "章节ID不能为空")
    private Long chapterId;

    /**
     * 最后播放位置（秒）
     */
    @ApiModelProperty(value = "最后播放位置（秒）", required = true)
    @NotNull(message = "播放位置不能为空")
    @Min(value = 0, message = "播放位置不能小于0")
    private Integer lastPosition;

    /**
     * 已学习时长（秒）
     */
    @ApiModelProperty(value = "已学习时长（秒）", required = true)
    @NotNull(message = "学习时长不能为空")
    @Min(value = 0, message = "学习时长不能小于0")
    private Integer duration;
}
