package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息 VO
 *
 * @author feng
 */
@Data
@ApiModel("用户信息")
public class UserInfoVO {

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
     * 头像URL
     */
    @ApiModelProperty("头像URL")
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
     * 性别：0-保密，1-男，2-女
     */
    @ApiModelProperty("性别：0-保密，1-男，2-女")
    private Integer gender;

    /**
     * 状态：0-禁用，1-正常
     */
    @ApiModelProperty("状态：0-禁用，1-正常")
    private Integer status;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
