package com.example.onlineedu.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 课程评价请求 DTO
 *
 * @author feng
 */
@Data
@ApiModel("课程评价请求")
public class CourseReviewDTO {

    /**
     * 课程ID（提交评价时必填，修改时由路径变量提供）
     */
    @ApiModelProperty("课程ID")
    private Long courseId;

    /**
     * 评分 1-5
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    @ApiModelProperty(value = "评分（1-5）", required = true)
    private Integer rating;

    /**
     * 评价内容
     */
    @ApiModelProperty("评价内容")
    private String content;
}
