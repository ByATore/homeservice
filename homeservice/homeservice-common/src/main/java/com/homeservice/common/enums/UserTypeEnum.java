package com.homeservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserTypeEnum {
    
    CUSTOMER(1, "客户"),
    WORKER(2, "服务人员"),
    ADMIN(3, "管理员"),
    SUPER_ADMIN(4, "超级管理员");
    
    private final Integer code;
    private final String desc;
    
    public static UserTypeEnum getByCode(Integer code) {
        for (UserTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}