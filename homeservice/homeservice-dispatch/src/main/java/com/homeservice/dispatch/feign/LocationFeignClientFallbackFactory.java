package com.homeservice.dispatch.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocationFeignClientFallbackFactory implements FallbackFactory<LocationFeignClient> {
    
    @Override
    public LocationFeignClient create(Throwable cause) {
        log.error("LocationFeignClient 调用失败: {}", cause.getMessage());
        return new LocationFeignClient() {
            @Override
            public com.homeservice.common.result.Result<java.util.List<java.util.Map<String, Object>>> getNearbyWorkers(Double latitude, Double longitude, Integer radius) {
                return com.homeservice.common.result.Result.error("定位服务不可用");
            }
        };
    }
}