package com.homeservice.auth.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.homeservice.auth.dto.LoginRequest;
import com.homeservice.auth.dto.RegisterRequest;
import com.homeservice.auth.service.AuthService;
import com.homeservice.common.util.JwtUtil;
import com.homeservice.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final RestTemplate restTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Map<String, Object> login(LoginRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        String url = "http://homeservice-user/user/username/" + request.getUsername();
        Map<String, Object> user = restTemplate.getForObject(url, Map.class);
        
        if (user == null || !Integer.valueOf(200).equals(user.get("code"))) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        
        Map<String, Object> userData = (Map<String, Object>) user.get("data");
        String encodedPassword = (String) userData.get("password");
        
        if (!passwordEncoder.matches(request.getPassword(), encodedPassword)) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }
        
        Object statusObj = userData.get("status");
        Integer status = statusObj != null ? Integer.valueOf(statusObj.toString()) : 0;
        if (status != 1) {
            result.put("success", false);
            result.put("message", "账号已被禁用");
            return result;
        }
        
        Long userId = Long.valueOf(userData.get("id").toString());
        String username = (String) userData.get("username");
        Integer userType = (Integer) userData.get("userType");
        
        String token = jwtUtil.generateToken(userId, username, String.valueOf(userType));
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        
        redisUtil.set("token:" + userId, token, 24, TimeUnit.HOURS);
        redisUtil.set("refresh:" + refreshToken, userId.toString(), 7, TimeUnit.DAYS);
        
        result.put("success", true);
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("userId", userId);
        result.put("username", username);
        result.put("userType", userType);
        return result;
    }

    @Override
    public boolean register(RegisterRequest request) {
        String url = "http://homeservice-user/user/username/" + request.getUsername();
        Map<String, Object> user = restTemplate.getForObject(url, Map.class);
        
        if (user != null && Integer.valueOf(200).equals(user.get("code"))) {
            return false;
        }
        
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("username", request.getUsername());
        newUser.put("password", passwordEncoder.encode(request.getPassword()));
        newUser.put("phone", request.getPhone());
        newUser.put("email", request.getEmail());
        newUser.put("status", 1);
        newUser.put("userType", 1);
        newUser.put("balance", 0);
        
        url = "http://homeservice-user/user";
        Map<String, Object> result = restTemplate.postForObject(url, newUser, Map.class);
        
        return result != null && Integer.valueOf(200).equals(result.get("code"));
    }

    @Override
    public boolean logout(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            redisUtil.delete("token:" + userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        Map<String, Object> result = new HashMap<>();
        
        String userIdStr = redisUtil.get("refresh:" + refreshToken);
        if (userIdStr == null) {
            result.put("success", false);
            result.put("message", "刷新令牌无效或已过期");
            return result;
        }
        
        Long userId = Long.parseLong(userIdStr);
        String url = "http://homeservice-user/user/" + userId;
        Map<String, Object> user = restTemplate.getForObject(url, Map.class);
        
        if (user == null || !Integer.valueOf(200).equals(user.get("code"))) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        
        Map<String, Object> userData = (Map<String, Object>) user.get("data");
        String username = (String) userData.get("username");
        Integer userType = (Integer) userData.get("userType");
        
        String newToken = jwtUtil.generateToken(userId, username, String.valueOf(userType));
        String newRefreshToken = UUID.randomUUID().toString().replace("-", "");
        
        redisUtil.delete("refresh:" + refreshToken);
        redisUtil.set("token:" + userId, newToken, 24, TimeUnit.HOURS);
        redisUtil.set("refresh:" + newRefreshToken, userId.toString(), 7, TimeUnit.DAYS);
        
        result.put("success", true);
        result.put("token", newToken);
        result.put("refreshToken", newRefreshToken);
        return result;
    }

    @Override
    public Map<String, Object> wechatLogin(String code, String nickname, String avatarUrl) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 调用微信接口 jscode2session 换取 openid 和 session_key
            String appId = System.getProperty("wechat.miniapp.appid", "your_appid");
            String secret = System.getProperty("wechat.miniapp.secret", "your_secret");
            String wxUrl = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    appId, secret, code);

            String wxResponse = restTemplate.getForObject(wxUrl, String.class);
            JSONObject wxJson = JSONUtil.parseObj(wxResponse);

            String openid = wxJson.getStr("openid");
            String sessionKey = wxJson.getStr("session_key");

            if (openid == null) {
                log.error("微信登录失败: errcode={}, errmsg={}",
                        wxJson.getStr("errcode"), wxJson.getStr("errmsg"));
                result.put("success", false);
                result.put("message", "微信登录失败: " + wxJson.getStr("errmsg", "未知错误"));
                return result;
            }

            // 根据 openid 查找或创建用户
            String url = "http://homeservice-user/user/openid/" + openid;
            Map<String, Object> userResp = restTemplate.getForObject(url, Map.class);
            boolean isNewUser = false;
            Map<String, Object> userData;

            if (userResp == null || !Integer.valueOf(200).equals(userResp.get("code"))) {
                // 创建新用户
                isNewUser = true;
                Map<String, Object> newUser = new HashMap<>();
                newUser.put("username", "wx_" + openid.substring(0, 12));
                newUser.put("password", "");
                newUser.put("openid", openid);
                newUser.put("nickname", nickname);
                newUser.put("avatar", avatarUrl);
                newUser.put("status", 1);
                newUser.put("userType", 1);
                newUser.put("balance", 0);

                url = "http://homeservice-user/user";
                Map<String, Object> createResp = restTemplate.postForObject(url, newUser, Map.class);
                if (createResp == null || !Integer.valueOf(200).equals(createResp.get("code"))) {
                    result.put("success", false);
                    result.put("message", "创建用户失败");
                    return result;
                }
                userData = (Map<String, Object>) createResp.get("data");
            } else {
                userData = (Map<String, Object>) userResp.get("data");
                // 更新用户信息
                if (nickname != null && !nickname.isEmpty()) {
                    userData.put("nickname", nickname);
                }
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    userData.put("avatar", avatarUrl);
                }
            }

            Long userId = Long.valueOf(userData.get("id").toString());
            String username = (String) userData.getOrDefault("username", "wx_" + openid.substring(0, 8));

            // 生成 token
            String token = jwtUtil.generateToken(userId, username, "1");
            String refreshToken = UUID.randomUUID().toString().replace("-", "");

            redisUtil.set("token:" + userId, token, 24, TimeUnit.HOURS);
            redisUtil.set("refresh:" + refreshToken, userId.toString(), 7, TimeUnit.DAYS);

            // 缓存 session_key 用于后续业务
            redisUtil.set("wx:session_key:" + openid, sessionKey, 2, TimeUnit.HOURS);

            result.put("success", true);
            result.put("token", token);
            result.put("refreshToken", refreshToken);
            result.put("userId", userId);
            result.put("username", username);
            result.put("isNewUser", isNewUser);
            return result;
        } catch (Exception e) {
            log.error("微信登录异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "微信登录异常: " + e.getMessage());
            return result;
        }
    }
}