package com.example.onlineedu.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录 DTO
 *
 * @author feng
 */
@Data
@ApiModel("用户登录请求")
public class LoginDTO {

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true, example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 登录端类型（必填）
     * app    - 用户端（需要 STUDENT 角色）
     * teacher - 讲师端（需要 TEACHER 角色）
     * admin  - 管理员端（需要 ADMIN 角色）
     */
    @ApiModelProperty(value = "登录端类型：app / teacher / admin", required = true, example = "app")
    @NotBlank(message = "登录端类型不能为空")
    private String loginType;
}
