package com.homeservice.dispatch.feign;

import com.homeservice.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "homeservice-worker", path = "/worker", fallbackFactory = WorkerFeignClientFallbackFactory.class)
public interface WorkerFeignClient {
    
    @GetMapping("/available")
    Result<List<Map<String, Object>>> getAvailableWorkers(@RequestParam("cityCode") String cityCode);
    
    @GetMapping("/{id}")
    Result<Map<String, Object>> getWorkerById(@PathVariable("id") Long id);
    
    @GetMapping("/{id}/dispatch")
    Result<Void> notifyDispatch(@PathVariable("id") Long id, @RequestParam("orderId") Long orderId);
}