package com.homeservice.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账号已禁用"),
    USER_TOKEN_EXPIRED(1004, "Token已过期"),
    USER_TOKEN_INVALID(1005, "Token无效"),
    
    ORDER_NOT_EXIST(2001, "订单不存在"),
    ORDER_STATUS_ERROR(2002, "订单状态错误"),
    ORDER_CANNOT_CANCEL(2003, "订单无法取消"),
    ORDER_CANNOT_REFUND(2004, "订单无法退款"),
    
    WORKER_NOT_EXIST(3001, "服务人员不存在"),
    WORKER_NOT_AVAILABLE(3002, "服务人员不可用"),
    WORKER_CERTIFICATION_EXPIRED(3003, "服务人员认证已过期"),
    
    PAYMENT_FAILED(4001, "支付失败"),
    PAYMENT_NOT_EXIST(4002, "支付记录不存在"),
    REFUND_FAILED(4003, "退款失败"),
    
    SERVICE_NOT_EXIST(5001, "服务不存在"),
    SERVICE_CATEGORY_NOT_EXIST(5002, "服务分类不存在"),
    
    COUPON_NOT_EXIST(6001, "优惠券不存在"),
    COUPON_EXPIRED(6002, "优惠券已过期"),
    COUPON_USED(6003, "优惠券已使用"),
    COUPON_LIMIT_EXCEEDED(6004, "优惠券领取数量超限"),
    
    FILE_UPLOAD_FAILED(7001, "文件上传失败"),
    FILE_SIZE_EXCEEDED(7002, "文件大小超限"),
    FILE_TYPE_NOT_SUPPORTED(7003, "文件类型不支持");
    
    private final Integer code;
    private final String message;
}