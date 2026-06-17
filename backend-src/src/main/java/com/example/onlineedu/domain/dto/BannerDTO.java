package com.example.onlineedu.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 轮播图 DTO
 *
 * @author feng
 */
@Data
@ApiModel("轮播图请求参数")
public class BannerDTO {

    @ApiModelProperty(value = "标题", required = true, example = "精品Java课程")
    @NotBlank(message = "标题不能为空")
    private String title;

    @ApiModelProperty(value = "图片地址", required = true, example = "http://xxxx/banner.jpg")
    @NotBlank(message = "图片地址不能为空")
    private String imageUrl;

    @ApiModelProperty(value = "关联课程ID（可选）", example = "1")
    private Long courseId;

    @ApiModelProperty(value = "排序（值越小越靠前）", example = "0")
    private Integer sort = 0;

    @ApiModelProperty(value = "状态：0-禁用，1-启用", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
