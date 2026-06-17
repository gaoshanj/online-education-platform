package com.example.onlineedu.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程评价列表项 VO
 *
 * @author feng
 */
@Data
@ApiModel("课程评价列表项")
public class CourseReviewVO {

    @ApiModelProperty("评价ID")
    private Long id;

    @ApiModelProperty("课程ID")
    private Long courseId;

    @ApiModelProperty("评价用户ID")
    private Long userId;

    @ApiModelProperty("用户昵称")
    private String userNickname;

    @ApiModelProperty("用户头像")
    private String userAvatar;

    @ApiModelProperty("评分（1-5）")
    private Integer rating;

    @ApiModelProperty("评价内容")
    private String content;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("当前用户是否已点赞该评价")
    private Boolean isLiked;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
