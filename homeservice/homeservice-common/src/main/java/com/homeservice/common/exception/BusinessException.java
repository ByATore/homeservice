package com.homeservice.common.exception;

import com.homeservice.common.result.ErrorCode;

public class BusinessException extends BaseException {
    
    public BusinessException(String message) {
        super(ErrorCode.ERROR.getCode(), message);
    }
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public BusinessException(Integer code, String message) {
        super(code, message);
    }
}