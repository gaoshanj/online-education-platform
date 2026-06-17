package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程分类 VO
 *
 * @author feng
 */
@Data
@ApiModel("课程分类响应")
public class CourseCategoryVO {

    /**
     * 分类ID
     */
    @ApiModelProperty("分类ID")
    private Long id;

    /**
     * 分类名称
     */
    @ApiModelProperty("分类名称")
    private String categoryName;

    /**
     * 父分类ID
     */
    @ApiModelProperty("父分类ID")
    private Long parentId;

    /**
     * 排序序号
     */
    @ApiModelProperty("排序序号")
    private Integer sortOrder;

    /**
     * 分类图标URL
     */
    @ApiModelProperty("分类图标URL")
    private String icon;

    /**
     * 分类描述
     */
    @ApiModelProperty("分类描述")
    private String description;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 子分类列表（用于树形结构）
     */
    @ApiModelProperty("子分类列表")
    private List<CourseCategoryVO> children;
}
