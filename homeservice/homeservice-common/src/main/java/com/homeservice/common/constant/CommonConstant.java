package com.homeservice.common.constant;

public class CommonConstant {
    
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    
    public static final String REDIS_KEY_PREFIX = "homeservice:";
    public static final String REDIS_KEY_TOKEN = REDIS_KEY_PREFIX + "token:";
    public static final String REDIS_KEY_VERIFY_CODE = REDIS_KEY_PREFIX + "verify:";
    public static final String REDIS_KEY_USER_INFO = REDIS_KEY_PREFIX + "user:info:";
    public static final String REDIS_KEY_WORKER_LOCATION = REDIS_KEY_PREFIX + "worker:location:";
    public static final String REDIS_KEY_DISPATCH_QUEUE = REDIS_KEY_PREFIX + "dispatch:queue";
    public static final String REDIS_KEY_ORDER_LOCK = REDIS_KEY_PREFIX + "order:lock:";
    
    public static final String DEFAULT_PASSWORD = "123456";
    
    public static final int PAGE_DEFAULT_SIZE = 10;
    public static final int PAGE_MAX_SIZE = 100;
    
    public static final int VERIFY_CODE_LENGTH = 6;
    public static final int VERIFY_CODE_EXPIRE_MINUTES = 5;
    
    public static final int TOKEN_EXPIRE_SECONDS = 86400;
    public static final int REFRESH_TOKEN_EXPIRE_SECONDS = 604800;
    
    public static final String ORDER_NO_PREFIX = "HS";
    public static final String PAYMENT_NO_PREFIX = "PAY";
    public static final String WORKER_NO_PREFIX = "WK";
}