package com.homeservice.dispatch.feign;

import com.homeservice.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "homeservice-config", path = "/location/worker", fallbackFactory = LocationFeignClientFallbackFactory.class)
public interface LocationFeignClient {

    @GetMapping("/nearby")
    Result<List<Map<String, Object>>> getNearbyWorkers(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam(value = "radius", defaultValue = "5000") Integer radius);

    @GetMapping("/{workerId}")
    Result<Map<String, Object>> getWorkerLocation(@PathVariable("workerId") Long workerId);
}