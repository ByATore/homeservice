package com.homeservice.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {"com.homeservice"}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.homeservice\\.common\\.config\\.GlobalExceptionHandler"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.homeservice\\.common\\.config\\.MybatisPlusConfig"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.homeservice\\.common\\.service\\.MqMessageService"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.homeservice\\.common\\.scheduler\\.MqMessageRetryScheduler")
})
@EnableDiscoveryClient
public class GatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}