package com.homeservice.auth.service.impl;

import com.homeservice.auth.dto.LoginRequest;
import com.homeservice.auth.dto.RegisterRequest;
import com.homeservice.common.util.JwtUtil;
import com.homeservice.common.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl 单元测试")
class AuthServiceImplTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private Map<String, Object> mockUserResponse;
    private Map<String, Object> mockUserData;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("13800138000");
        registerRequest.setEmail("test@example.com");

        mockUserData = new HashMap<>();
        mockUserData.put("id", 1);
        mockUserData.put("username", "testuser");
        mockUserData.put("password", new BCryptPasswordEncoder().encode("password123"));
        mockUserData.put("userType", 1);
        mockUserData.put("status", 1);

        mockUserResponse = new HashMap<>();
        mockUserResponse.put("code", "200");
        mockUserResponse.put("data", mockUserData);
    }

    @Nested
    @DisplayName("login 方法测试")
    class LoginTests {

        @Test
        @DisplayName("应该成功登录并返回 token")
        void shouldLoginSuccessfully() {
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(mockUserResponse);
            when(jwtUtil.generateToken(eq(1L), eq("testuser"), eq("1")))
                    .thenReturn("mock-jwt-token");

            Map<String, Object> result = authService.login(loginRequest);

            assertTrue((Boolean) result.get("success"));
            assertEquals("mock-jwt-token", result.get("token"));
            assertNotNull(result.get("refreshToken"));
            assertEquals(1L, result.get("userId"));
            assertEquals("testuser", result.get("username"));
            assertEquals(1, result.get("userType"));

            verify(redisUtil).set(eq("token:1"), eq("mock-jwt-token"), eq(24L), eq(TimeUnit.HOURS));
            verify(redisUtil).set(anyString(), eq("1"), eq(7L), eq(TimeUnit.DAYS));
        }

        @Test
        @DisplayName("用户不存在时应返回失败")
        void shouldFailWhenUserNotFound() {
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(null);

            Map<String, Object> result = authService.login(loginRequest);

            assertFalse((Boolean) result.get("success"));
            assertEquals("用户不存在", result.get("message"));
        }

        @Test
        @DisplayName("用户接口返回非200时应返回失败")
        void shouldFailWhenUserResponseNot200() {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "500");
            errorResponse.put("message", "内部错误");
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(errorResponse);

            Map<String, Object> result = authService.login(loginRequest);

            assertFalse((Boolean) result.get("success"));
            assertEquals("用户不存在", result.get("message"));
        }

        @Test
        @DisplayName("密码错误时应返回失败")
        void shouldFailWhenPasswordWrong() {
            loginRequest.setPassword("wrongpassword");
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(mockUserResponse);

            Map<String, Object> result = authService.login(loginRequest);

            assertFalse((Boolean) result.get("success"));
            assertEquals("密码错误", result.get("message"));
        }

        @Test
        @DisplayName("账号被禁用时应返回失败")
        void shouldFailWhenAccountDisabled() {
            mockUserData.put("status", 0);
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(mockUserResponse);

            Map<String, Object> result = authService.login(loginRequest);

            assertFalse((Boolean) result.get("success"));
            assertEquals("账号已被禁用", result.get("message"));
        }
    }

    @Nested
    @DisplayName("register 方法测试")
    class RegisterTests {

        @Test
        @DisplayName("用户名不存在时应成功注册")
        void shouldRegisterSuccessfully() {
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(null);
            Map<String, Object> createResponse = new HashMap<>();
            createResponse.put("code", "200");
            when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                    .thenReturn(createResponse);

            boolean result = authService.register(registerRequest);

            assertTrue(result);
            verify(restTemplate).postForObject(anyString(), any(), eq(Map.class));
        }

        @Test
        @DisplayName("用户名已存在时应返回false")
        void shouldFailWhenUsernameExists() {
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(mockUserResponse);

            boolean result = authService.register(registerRequest);

            assertFalse(result);
            verify(restTemplate, never()).postForObject(anyString(), any(), eq(Map.class));
        }

        @Test
        @DisplayName("创建用户接口返回非200时应返回false")
        void shouldFailWhenCreateUserFails() {
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(null);
            Map<String, Object> createResponse = new HashMap<>();
            createResponse.put("code", "500");
            when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                    .thenReturn(createResponse);

            boolean result = authService.register(registerRequest);

            assertFalse(result);
        }

        @Test
        @DisplayName("用户名查询时接口返回非200且data不为空，应允许注册")
        void shouldRegisterWhenUserCheckReturnsNon200WithNullData() {
            Map<String, Object> checkResponse = new HashMap<>();
            checkResponse.put("code", "404");
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(checkResponse);
            Map<String, Object> createResponse = new HashMap<>();
            createResponse.put("code", "200");
            when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                    .thenReturn(createResponse);

            boolean result = authService.register(registerRequest);

            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("logout 方法测试")
    class LogoutTests {

        @Test
        @DisplayName("应该成功登出并删除token")
        void shouldLogoutSuccessfully() {
            when(jwtUtil.getUserIdFromToken("valid-token"))
                    .thenReturn(1L);

            boolean result = authService.logout("valid-token");

            assertTrue(result);
            verify(redisUtil).delete("token:1");
        }

        @Test
        @DisplayName("token解析失败时应返回false")
        void shouldFailWhenTokenInvalid() {
            when(jwtUtil.getUserIdFromToken("invalid-token"))
                    .thenThrow(new RuntimeException("Invalid token"));

            boolean result = authService.logout("invalid-token");

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("refreshToken 方法测试")
    class RefreshTokenTests {

        @Test
        @DisplayName("应该成功刷新token")
        void shouldRefreshTokenSuccessfully() {
            when(redisUtil.get("refresh:valid-refresh-token"))
                    .thenReturn("1");
            when(restTemplate.getForObject(eq("http://homeservice-user/user/1"), eq(Map.class)))
                    .thenReturn(mockUserResponse);
            when(jwtUtil.generateToken(eq(1L), eq("testuser"), eq("1")))
                    .thenReturn("new-jwt-token");

            Map<String, Object> result = authService.refreshToken("valid-refresh-token");

            assertTrue((Boolean) result.get("success"));
            assertEquals("new-jwt-token", result.get("token"));
            assertNotNull(result.get("refreshToken"));

            verify(redisUtil).delete("refresh:valid-refresh-token");
            verify(redisUtil).set(eq("token:1"), eq("new-jwt-token"), eq(24L), eq(TimeUnit.HOURS));
            verify(redisUtil).set(anyString(), eq("1"), eq(7L), eq(TimeUnit.DAYS));
        }

        @Test
        @DisplayName("refreshToken不存在时应返回失败")
        void shouldFailWhenRefreshTokenNotFound() {
            when(redisUtil.get("refresh:invalid-refresh-token"))
                    .thenReturn(null);

            Map<String, Object> result = authService.refreshToken("invalid-refresh-token");

            assertFalse((Boolean) result.get("success"));
            assertEquals("刷新令牌无效或已过期", result.get("message"));
        }

        @Test
        @DisplayName("用户不存在时应返回失败")
        void shouldFailWhenUserNotFoundDuringRefresh() {
            when(redisUtil.get("refresh:valid-refresh-token"))
                    .thenReturn("1");
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(null);

            Map<String, Object> result = authService.refreshToken("valid-refresh-token");

            assertFalse((Boolean) result.get("success"));
            assertEquals("用户不存在", result.get("message"));
        }

        @Test
        @DisplayName("用户接口返回非200时应返回失败")
        void shouldFailWhenUserResponseNot200DuringRefresh() {
            when(redisUtil.get("refresh:valid-refresh-token"))
                    .thenReturn("1");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "500");
            when(restTemplate.getForObject(anyString(), eq(Map.class)))
                    .thenReturn(errorResponse);

            Map<String, Object> result = authService.refreshToken("valid-refresh-token");

            assertFalse((Boolean) result.get("success"));
            assertEquals("用户不存在", result.get("message"));
        }
    }
}