package com.homeservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkerStatusEnum {
    
    OFFLINE(0, "下线"),
    ONLINE(1, "在线空闲"),
    BUSY(2, "服务中"),
    REST(3, "休假");
    
    private final Integer code;
    private final String desc;
    
    public static WorkerStatusEnum getByCode(Integer code) {
        for (WorkerStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}