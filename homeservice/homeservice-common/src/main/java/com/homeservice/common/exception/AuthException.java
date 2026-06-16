package com.homeservice.common.exception;

import com.homeservice.common.result.ErrorCode;

public class AuthException extends BaseException {
    
    public AuthException(String message) {
        super(ErrorCode.UNAUTHORIZED.getCode(), message);
    }
    
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}