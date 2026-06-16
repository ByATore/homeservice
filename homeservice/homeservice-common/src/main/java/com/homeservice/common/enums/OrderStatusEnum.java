package com.homeservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    
    PENDING(1, "待支付"),
    PAID(2, "待服务"),
    DISPATCHED(3, "已派单"),
    ACCEPTED(4, "已接单"),
    IN_PROGRESS(5, "服务中"),
    COMPLETED(6, "待评价"),
    CONFIRMED(7, "已完成"),
    CANCELLED(8, "已取消"),
    REFUNDED(9, "已退款");
    
    private final Integer code;
    private final String desc;
    
    public static OrderStatusEnum getByCode(Integer code) {
        for (OrderStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
    
    public boolean canCancel() {
        return this == PENDING || this == PAID || this == DISPATCHED;
    }
    
    public boolean canRefund() {
        return this == PAID || this == DISPATCHED || this == ACCEPTED;
    }
}