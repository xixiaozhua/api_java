package com.example.apiservice.common.enums;

public enum ResultCode {
    SUCCESS(0, "success"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    VALIDATION_ERROR(40003, "Validation Error"),
    INTERNAL_SERVER_ERROR(50000, "Internal Server Error"),
    INVALID_CODE(40010, "验证码错误或已过期"),
    USER_NOT_FOUND(40011, "用户不存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}