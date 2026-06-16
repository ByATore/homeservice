package com.homeservice.config.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigType {

    TEXT(1, "文本"),
    JSON(2, "JSON"),
    NUMBER(3, "数字"),
    BOOLEAN(4, "布尔");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String desc;

    public static ConfigType getByCode(Integer code) {
        for (ConfigType value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return TEXT;
    }
}
