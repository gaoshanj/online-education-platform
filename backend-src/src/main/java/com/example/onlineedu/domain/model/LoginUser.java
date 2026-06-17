package com.example.onlineedu.domain.model;

import lombok.Data;

import java.util.List;

/**
 * 登录用户信息模型
 * 用于存储在 ThreadLocal 中
 *
 * @author feng
 */
@Data
public class LoginUser {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色列表
     */
    private List<String> roles;
}
