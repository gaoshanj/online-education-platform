package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 登录响应 VO
 *
 * @author feng
 */
@Data
@ApiModel("登录响应")
public class LoginVO {

    /**
     * JWT Token
     */
    @ApiModelProperty("JWT Token")
    private String token;

    /**
     * 用户信息
     */
    @ApiModelProperty("用户信息")
    private UserInfoVO userInfo;

    /**
     * 角色列表
     */
    @ApiModelProperty("角色列表")
    private List<String> roles;
}
