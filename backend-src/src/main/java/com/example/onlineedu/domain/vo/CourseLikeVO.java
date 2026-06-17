package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 课程点赞操作响应 VO
 *
 * @author feng
 */
@Data
@ApiModel("点赞操作响应")
public class CourseLikeVO {

    /**
     * 课程当前点赞总数
     */
    @ApiModelProperty("课程当前点赞总数")
    private Integer likeCount;

    /**
     * 当前用户是否已点赞
     */
    @ApiModelProperty("当前用户是否已点赞")
    private Boolean isLiked;
}
