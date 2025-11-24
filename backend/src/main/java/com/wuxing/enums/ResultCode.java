package com.wuxing.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 * 
 * @author wuxing
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    
    VALIDATION_ERROR(4001, "参数验证失败"),
    USERNAME_EXISTS(4002, "用户名已存在"),
    USER_NOT_FOUND(4003, "用户不存在"),
    PASSWORD_ERROR(4004, "密码错误"),
    ACCOUNT_DISABLED(4005, "账号已被禁用"),
    
    DATABASE_ERROR(5001, "数据库错误"),
    REDIS_ERROR(5002, "Redis错误"),
    FILE_UPLOAD_ERROR(5003, "文件上传失败");

    private final Integer code;
    private final String message;
}
