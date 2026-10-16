package com.example.demo1.common;

import java.io.Serializable;

/**
 * 统一响应体
 */
public record Result<T>(int code, String message, T data, long timestamp) implements Serializable {

    private static final int SUCCESS_CODE = 0;
    private static final String SUCCESS_MESSAGE = "ok";

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, data, System.currentTimeMillis());
    }

    public static <T> Result<T> fail(String message) {
        return fail(BusinessException.DEFAULT_CODE, message);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }
}
