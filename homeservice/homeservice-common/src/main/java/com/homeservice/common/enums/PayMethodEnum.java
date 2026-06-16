package com.homeservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayMethodEnum {
    
    WECHAT(1, "微信支付"),
    ALIPAY(2, "支付宝"),
    BALANCE(3, "余额支付"),
    BANK_CARD(4, "银行卡");
    
    private final Integer code;
    private final String desc;
    
    public static PayMethodEnum getByCode(Integer code) {
        for (PayMethodEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}