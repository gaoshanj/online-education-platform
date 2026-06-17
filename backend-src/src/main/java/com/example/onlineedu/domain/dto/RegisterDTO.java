package com.example.onlineedu.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户注册 DTO
 *
 * @author feng
 */
@Data
@ApiModel("用户注册请求")
public class RegisterDTO {

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true, example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名只能包含字母、数字、下划线，长度4-20位")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true, example = "123456")
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^.{6,20}$", message = "密码长度必须在6-20位之间")
    private String password;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", example = "张三")
    private String nickname;

    /**
     * 邮箱（必填，注册前需先发送验证码）
     */
    @ApiModelProperty(value = "邮箱", required = true, example = "zhangsan@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱格式不正确")
    private String email;

    /**
     * 邮箱验证码（必填，通过发送验证码接口获取）
     */
    @ApiModelProperty(value = "邮箱验证码", required = true, example = "123456")
    @NotBlank(message = "邮箱验证码不能为空")
    private String emailCode;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
