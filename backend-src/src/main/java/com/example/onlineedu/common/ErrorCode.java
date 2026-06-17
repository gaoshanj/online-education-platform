package com.example.onlineedu.common;

import lombok.Getter;

/**
 * 业务错误码枚举
 * 统一定义所有业务错误类型
 *
 * @author feng
 */
@Getter
public enum ErrorCode {

    // ========== 通用错误码 ==========
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(500, "系统错误"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    // ========== 用户相关错误码 1xxx ==========
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户已存在"),
    USER_DISABLED(1003, "用户已被禁用"),
    USERNAME_OR_PASSWORD_ERROR(1004, "用户名或密码错误"),
    USER_NOT_LOGIN(1005, "用户未登录"),
    TOKEN_INVALID(1006, "Token无效或已过期"),
    TOKEN_EXPIRED(1007, "Token已过期"),
    PERMISSION_DENIED(1008, "权限不足"),
    OLD_PASSWORD_ERROR(1009, "原密码错误"),
    NO_AUTH(1010, "无权限操作"),

    // ========== 课程相关错误码 2xxx ==========
    COURSE_NOT_EXIST(2001, "课程不存在"),
    COURSE_NOT_PUBLISHED(2002, "课程未发布"),
    COURSE_ALREADY_EXIST(2003, "课程已存在"),
    CATEGORY_NOT_EXIST(2004, "分类不存在"),
    CATEGORY_ALREADY_EXIST(2005, "分类已存在"),
    CATEGORY_HAS_CHILDREN(2006, "分类下有子分类"),
    CHAPTER_NOT_EXIST(2007, "章节不存在"),

    // ========== 讲师相关错误码 6xxx ==========
    TEACHER_NOT_EXIST(6001, "讲师不存在"),
    NOT_COURSE_TEACHER(6002, "您不是该课程的讲师"),

    // ========== Redis相关错误码 7xxx ==========
    REDIS_ERROR(7001, "Redis操作失败"),
    CACHE_ERROR(7002, "缓存操作失败"),

    // ========== 文件相关错误码 5xxx ==========
    FILE_UPLOAD_ERROR(5001, "文件上传失败"),
    FILE_TYPE_ERROR(5002, "文件类型不支持"),
    FILE_SIZE_ERROR(5003, "文件大小超出限制"),
    FILE_NOT_EXIST(5004, "文件不存在");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
