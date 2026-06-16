package com.homeservice.notify.service;

import java.util.Map;

public interface NotifyService {
    
    void send(String type, String target, String template, Map<String, String> params);
    
    void sendSmsCode(String phone, String code);
    
    void sendOrderStatusNotify(String phone, String orderNo, String status);
    
    void sendDispatchNotify(String phone, String orderNo, String workerName);
    
    void sendPaymentSuccessNotify(String phone, String orderNo, String amount);
}