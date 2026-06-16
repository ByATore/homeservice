package com.homeservice.auth.service;

import com.homeservice.auth.dto.LoginRequest;
import com.homeservice.auth.dto.RegisterRequest;
import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginRequest request);
    boolean register(RegisterRequest request);
    boolean logout(String token);
    Map<String, Object> refreshToken(String refreshToken);

    /**
     * 微信登录
     */
    Map<String, Object> wechatLogin(String code, String nickname, String avatarUrl);
}