package com.homeservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CertStatusEnum {
    
    UNAUTHORIZED(0, "未认证"),
    REVIEWING(1, "审核中"),
    CERTIFIED(2, "已认证"),
    REJECTED(3, "认证失败");
    
    private final Integer code;
    private final String desc;
    
    public static CertStatusEnum getByCode(Integer code) {
        for (CertStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}