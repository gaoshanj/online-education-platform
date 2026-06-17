package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 轮播图 VO
 *
 * @author feng
 */
@Data
@ApiModel("轮播图信息")
public class BannerVO {

    @ApiModelProperty("轮播图ID")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("图片地址")
    private String imageUrl;

    @ApiModelProperty("关联课程ID（前端点击跳转用）")
    private Long courseId;

    @ApiModelProperty("关联课程名称")
    private String courseName;


    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
}
