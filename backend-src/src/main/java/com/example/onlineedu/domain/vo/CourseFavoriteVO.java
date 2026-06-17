package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 课程收藏操作响应 VO
 *
 * @author feng
 */
@Data
@ApiModel("收藏操作响应")
public class CourseFavoriteVO {

    /**
     * 课程当前收藏总数
     */
    @ApiModelProperty("课程当前收藏总数")
    private Integer favoriteCount;

    /**
     * 当前用户是否已收藏
     */
    @ApiModelProperty("当前用户是否已收藏")
    private Boolean isFavorited;
}
