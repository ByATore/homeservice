package com.homeservice.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final int MAX_REQUESTS_PER_SECOND = 100;
    private static final String RATE_LIMIT_KEY = "homeservice:rate-limit:";
    
    public RateLimitFilter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = getClientIp(exchange);
        String key = RATE_LIMIT_KEY + clientIp;
        
        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null) {
                if (count == 1L) {
                    redisTemplate.expire(key, 1, TimeUnit.SECONDS);
                }
                if (count > MAX_REQUESTS_PER_SECOND) {
                    return rateLimit(exchange, "请求过于频繁，请稍后重试");
                }
            }
        } catch (Exception e) {
            log.warn("限流检查异常，放行请求: {}", e.getMessage());
        }
        
        return chain.filter(exchange);
    }
    
    private String getClientIp(ServerWebExchange exchange) {
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = exchange.getRequest().getRemoteAddress() != null ? 
                 exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
        }
        return ip;
    }
    
    private Mono<Void> rateLimit(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"code\":429,\"message\":\"%s\",\"data\":null,\"timestamp\":%d}", message, System.currentTimeMillis());
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
    
    @Override
    public int getOrder() {
        return -200;
    }
}