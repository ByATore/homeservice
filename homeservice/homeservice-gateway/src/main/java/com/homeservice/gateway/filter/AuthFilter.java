package com.homeservice.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * Stub AuthFilter - 认证已由 AuthGlobalFilter 全局过滤器统一处理，
 * 此过滤器仅作为兼容 Nacos 配置中冗余的 AuthFilter 引用，不做任何额外处理。
 */
@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<Object> {

    public AuthFilter() {
        super(Object.class);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> chain.filter(exchange);
    }

    @Override
    public String name() {
        return "AuthFilter";
    }
}