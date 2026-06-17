package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程分类实体类
 *
 * @author feng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_category")
public class CourseCategoryEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 父分类ID（0表示一级分类）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 分类图标URL
     */
    @TableField("icon")
    private String icon;

    /**
     * 分类描述
     */
    @TableField("description")
    private String description;
}
