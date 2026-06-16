package com.homeservice.common.exception;

import com.homeservice.common.result.ErrorCode;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    
    private final Integer code;
    
    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
    
    public BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}