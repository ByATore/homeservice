package com.homeservice.dispatch.service.impl;

import com.homeservice.common.result.Result;
import com.homeservice.dispatch.feign.LocationFeignClient;
import com.homeservice.dispatch.feign.WorkerFeignClient;
import com.homeservice.dispatch.service.MatchService;
import com.homeservice.dispatch.strategy.CompositeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final WorkerFeignClient workerFeignClient;
    private final LocationFeignClient locationFeignClient;
    private final CompositeStrategy compositeStrategy;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Long matchBestWorker(Long orderId) {
        Map<String, Object> order = getOrderInfo(orderId);
        if (order == null) {
            log.warn("订单信息为空: orderId={}", orderId);
            return null;
        }

        String cityCode = order.get("cityCode") != null ? order.get("cityCode").toString() : "default";
        Result<List<Map<String, Object>>> workersResult = workerFeignClient.getAvailableWorkers(cityCode);
        if (workersResult == null || workersResult.getData() == null || workersResult.getData().isEmpty()) {
            log.warn("无可用服务人员: orderId={}, cityCode={}", orderId, cityCode);
            return null;
        }

        List<Map<String, Object>> workers = workersResult.getData();
        List<Map<String, Object>> ranked = compositeStrategy.rank(order, workers);

        for (Map<String, Object> worker : ranked) {
            Long workerId = Long.valueOf(worker.get("id").toString());
            String rejectKey = "homeservice:dispatch:reject:" + orderId + ":" + workerId;
            Boolean rejected = redisTemplate.hasKey(rejectKey);
            if (Boolean.FALSE.equals(rejected)) {
                log.info("匹配到最佳服务人员: orderId={}, workerId={}", orderId, workerId);
                return workerId;
            }
        }

        log.warn("所有服务人员均已拒绝该订单: orderId={}", orderId);
        return null;
    }

    private Map<String, Object> getOrderInfo(Long orderId) {
        String key = "homeservice:dispatch:order:" + orderId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof Map) {
            return (Map<String, Object>) cached;
        }
        return null;
    }
}