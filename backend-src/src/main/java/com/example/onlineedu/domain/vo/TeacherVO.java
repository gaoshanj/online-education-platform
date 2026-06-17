package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 讲师信息 VO
 *
 * @author feng
 */
@Data
@ApiModel("讲师信息")
public class TeacherVO {

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long id;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;

    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickname;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 课程总数
     */
    @ApiModelProperty("课程总数")
    private Integer courseCount;

    /**
     * 学生总数
     */
    @ApiModelProperty("学生总数")
    private Integer studentCount;

    /**
     * 课程浏览总数
     */
    @ApiModelProperty("课程浏览总数")
    private Integer viewCount;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 平均评分（加权均均，保留一位小数）
     */
    @ApiModelProperty("平均评分（1位小数）")
    private BigDecimal avgScore;

    /**
     * 点赞总数
     */
    @ApiModelProperty("点赞总数")
    private Integer likeCount;

    /**
     * 收藏总数
     */
    @ApiModelProperty("收藏总数")
    private Integer favoriteCount;

    /**
     * 评价总数
     */
    @ApiModelProperty("评价总数")
    private Integer reviewCount;
}
