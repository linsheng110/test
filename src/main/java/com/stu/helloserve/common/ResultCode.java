package com.stu.helloserve.common;
/**
 * 统一返回状态码枚举
 */
public enum ResultCode {
    // 成功
    SUCCESS(200, "操作成功"),
    // 系统错误
    SYSTEM_ERROR(500, "系统异常"),
    // 权限错误
    TOKEN_INVALID(401, "未授权，请先登录");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
