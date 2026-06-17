package com.example.onlineedu.exception;

/**
 * 千问API异常
 *
 * @author feng
 */
public class QwenApiException extends RuntimeException {

    public QwenApiException(String message) {
        super(message);
    }

    public QwenApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
