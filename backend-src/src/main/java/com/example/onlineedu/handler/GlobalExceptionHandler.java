package com.example.onlineedu.handler;

import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各类异常并返回标准格式
 *
 * @author feng
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常：{} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数校验异常：{} - {}", request.getRequestURI(), errorMessage);
        return Result.error(ErrorCode.PARAM_ERROR, errorMessage);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数绑定异常：{} - {}", request.getRequestURI(), errorMessage);
        return Result.error(ErrorCode.PARAM_ERROR, errorMessage);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        log.error("参数类型不匹配异常：{} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(ErrorCode.PARAM_ERROR, "参数类型不匹配：" + e.getName());
    }

    /**
     * 处理数据库唯一键冲突异常（如用户名重复注册）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        log.error("唯一键冲突：{} - {}", request.getRequestURI(), e.getMessage());
        // 根据错误信息判断具体字段
        String msg = e.getCause() != null ? e.getCause().getMessage() : "";
        if (msg.contains("username")) {
            return Result.error(ErrorCode.USER_ALREADY_EXIST, "用户名已存在");
        }
        if (msg.contains("email")) {
            return Result.error(ErrorCode.PARAM_ERROR, "邮箱已被注册");
        }
        return Result.error(ErrorCode.PARAM_ERROR, "数据已存在，请勿重复提交");
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常：{}", request.getRequestURI(), e);
        return Result.error(ErrorCode.SYSTEM_ERROR, "系统内部错误");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("非法参数异常：{} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(ErrorCode.PARAM_ERROR, e.getMessage());
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常：{}", request.getRequestURI(), e);
        return Result.error(ErrorCode.SYSTEM_ERROR, "系统内部错误，请联系管理员");
    }
}
