package com.example.demo1.common;

/**
 * 业务异常，携带业务状态码
 */
public class BusinessException extends RuntimeException {

    public static final int DEFAULT_CODE = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_FORBIDDEN = 403;

    private final int code;

    public BusinessException(String message) {
        this(DEFAULT_CODE, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(CODE_UNAUTHORIZED, message);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(CODE_FORBIDDEN, message);
    }
}
