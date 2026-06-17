package com.example.onlineedu.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 课程信息 DTO
 *
 * @author feng
 */
@Data
@ApiModel("课程信息请求")
public class CourseDTO {

    /**
     * 课程ID（更新时需要）
     */
    @ApiModelProperty("课程ID")
    private Long id;

    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称", required = true, example = "Java从入门到精通")
    @NotBlank(message = "课程名称不能为空")
    private String courseName;

    /**
     * 分类ID
     */
    @ApiModelProperty(value = "分类ID", required = true, example = "1")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 讲师ID
     */
    @ApiModelProperty(value = "讲师ID", required = true, example = "1")
    //@NotNull(message = "讲师ID不能为空")
    private Long teacherId;

    /**
     * 课程封面图
     */
    @ApiModelProperty(value = "课程封面图URL")
    private String coverImage;

    /**
     * 课程简介
     */
    @ApiModelProperty(value = "课程简介")
    private String description;



    /**
     * 课程难度：1-入门，2-初级，3-中级，4-高级
     */
    @ApiModelProperty(value = "课程难度", example = "1")
    private Integer level = 1;

    /**
     * 是否热门
     */
    @ApiModelProperty(value = "是否热门", example = "0")
    private Integer isHot = 0;

    /**
     * 是否推荐
     */
    @ApiModelProperty(value = "是否推荐", example = "0")
    private Integer isRecommend = 0;
}
