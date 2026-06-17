package com.example.onlineedu.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 课程分类 DTO
 *
 * @author feng
 */
@Data
@ApiModel("课程分类请求")
public class CourseCategoryDTO {

    /**
     * 分类ID（更新时需要）
     */
    @ApiModelProperty("分类ID")
    private Long id;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称", required = true, example = "前端开发")
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    /**
     * 父分类ID（0表示一级分类）
     */
    @ApiModelProperty(value = "父分类ID", example = "0")
    private Long parentId = 0L;

    /**
     * 排序序号
     */
    @ApiModelProperty(value = "排序序号", example = "1")
    private Integer sortOrder = 0;

    /**
     * 分类图标URL
     */
    @ApiModelProperty(value = "分类图标URL")
    private String icon;

    /**
     * 分类描述
     */
    @ApiModelProperty(value = "分类描述")
    private String description;
}
