package com.example.onlineedu.utils;

import com.example.onlineedu.domain.model.LoginUser;

/**
 * 用户上下文工具类
 * 使用 ThreadLocal 存储当前登录用户信息
 *
 * @author feng
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> userThreadLocal = new ThreadLocal<>();

    /**
     * 设置当前登录用户
     *
     * @param loginUser 登录用户信息
     */
    public static void setUser(LoginUser loginUser) {
        userThreadLocal.set(loginUser);
    }

    /**
     * 获取当前登录用户
     *
     * @return 登录用户信息
     */
    public static LoginUser getUser() {
        return userThreadLocal.get();
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        LoginUser user = getUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        LoginUser user = getUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 清除当前登录用户信息
     */
    public static void clear() {
        userThreadLocal.remove();
    }
}
