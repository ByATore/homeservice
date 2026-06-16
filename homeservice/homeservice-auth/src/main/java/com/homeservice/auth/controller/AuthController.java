package com.homeservice.auth.controller;

import com.homeservice.auth.dto.LoginRequest;
import com.homeservice.auth.dto.RegisterRequest;
import com.homeservice.auth.service.AuthService;
import com.homeservice.common.result.Result;
import com.homeservice.common.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> result = authService.login(request);
        if ((Boolean) result.get("success")) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", result.get("token"));
            data.put("refreshToken", result.get("refreshToken"));
            data.put("userId", result.get("userId"));
            data.put("username", result.get("username"));
            data.put("userType", result.get("userType"));
            return Result.success(data);
        } else {
            return Result.error((String) result.get("message"));
        }
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        boolean success = authService.register(request);
        return success ? Result.success() : Result.error("注册失败，用户名已存在");
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        boolean success = authService.logout(actualToken);
        return success ? Result.success() : Result.error("登出失败");
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/userinfo")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        try {
            Long userId = jwtUtil.getUserIdFromToken(actualToken);
            String username = jwtUtil.getUsernameFromToken(actualToken);

            String url = "http://homeservice-user/user/" + userId;
            Map<String, Object> userResult = restTemplate.getForObject(url, Map.class);

            if (userResult == null || !Integer.valueOf(200).equals(userResult.get("code"))) {
                return Result.error("用户不存在");
            }

            Map<String, Object> userData = (Map<String, Object>) userResult.get("data");
            Map<String, Object> data = new HashMap<>();
            data.put("id", userData.get("id"));
            data.put("username", userData.get("username"));
            data.put("avatar", userData.get("avatar"));
            data.put("name", userData.get("realName"));
            data.put("userType", userData.get("userType"));
            data.put("phone", userData.get("phone"));
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取用户信息失败");
        }
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken(@RequestParam String refreshToken) {
        Map<String, Object> result = authService.refreshToken(refreshToken);
        if ((Boolean) result.get("success")) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", result.get("token"));
            data.put("refreshToken", result.get("refreshToken"));
            return Result.success(data);
        } else {
            return Result.error((String) result.get("message"));
        }
    }

    @Operation(summary = "微信一键登录")
    @PostMapping("/wechat-login")
    public Result<Map<String, Object>> wechatLogin(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        String nickname = (String) request.getOrDefault("nickname", "");
        String avatarUrl = (String) request.getOrDefault("avatarUrl", "");

        if (code == null || code.isEmpty()) {
            return Result.error("微信登录code不能为空");
        }

        // 调用微信接口获取 openid 和 session_key
        try {
            Map<String, Object> wxResult = authService.wechatLogin(code, nickname, avatarUrl);
            if ((Boolean) wxResult.get("success")) {
                Map<String, Object> data = new HashMap<>();
                data.put("token", wxResult.get("token"));
                data.put("refreshToken", wxResult.get("refreshToken"));
                data.put("userId", wxResult.get("userId"));
                data.put("username", wxResult.get("username"));
                data.put("isNewUser", wxResult.getOrDefault("isNewUser", false));
                return Result.success(data);
            } else {
                return Result.error((String) wxResult.get("message"));
            }
        } catch (Exception e) {
            return Result.error("微信登录失败: " + e.getMessage());
        }
    }
}