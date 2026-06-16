package com.homeservice.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class LogFilter implements GlobalFilter, Ordered {
    
    private static final String START_TIME = "startTime";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(START_TIME, Instant.now());
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpRequest request = exchange.getRequest();
            Instant startTime = exchange.getAttribute(START_TIME);
            if (startTime != null) {
                long duration = Duration.between(startTime, Instant.now()).toMillis();
                String method = request.getMethod().name();
                String path = request.getURI().getPath();
                String query = request.getURI().getQuery();
                int statusCode = exchange.getResponse().getStatusCode() != null ? 
                                  exchange.getResponse().getStatusCode().value() : 0;
                
                log.info("{} {}?{} -> {} ({}ms)", method, path, query != null ? query : "", statusCode, duration);
            }
        }));
    }
    
    @Override
    public int getOrder() {
        return -300;
    }
}