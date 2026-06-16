package com.homeservice.statistics.feign;

import com.homeservice.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "homeservice-order", path = "/order")
public interface OrderFeignClient {

    @GetMapping("/status-distribution")
    Result<List<Map<String, Object>>> getStatusDistribution();
}
