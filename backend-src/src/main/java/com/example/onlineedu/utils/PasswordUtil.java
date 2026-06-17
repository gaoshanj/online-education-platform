package com.example.onlineedu.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密工具类
 * 使用 MD5 加密
 *
 * @author feng
 */
public class PasswordUtil {

    /**
     * MD5 加密
     *
     * @param password 原始密码
     * @return 加密后的密码（32位小写）
     */
    public static String encrypt(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // 转换为16进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * 验证密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return true 密码正确，false 密码错误
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encrypt(rawPassword).equals(encodedPassword);
    }

    /**
     * 加盐加密（可选，增强安全性）
     *
     * @param password 原始密码
     * @param salt     盐值
     * @return 加密后的密码
     */
    public static String encryptWithSalt(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("密码和盐值不能为空");
        }
        return encrypt(password + salt);
    }
}
