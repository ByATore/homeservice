package com.homeservice.dispatch.service.impl;

import com.homeservice.common.result.Result;
import com.homeservice.dispatch.feign.LocationFeignClient;
import com.homeservice.dispatch.feign.WorkerFeignClient;

import com.homeservice.dispatch.service.DispatchService;
import com.homeservice.dispatch.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchServiceImpl implements DispatchService {

    private final MatchService matchService;
    private final WorkerFeignClient workerFeignClient;
    private final LocationFeignClient locationFeignClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Long dispatch(Long orderId) {
        String lockKey = "homeservice:dispatch:lock:" + orderId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            log.warn("派单进行中，跳过重复派单: orderId={}", orderId);
            return null;
        }

        try {
            Long workerId = matchService.matchBestWorker(orderId);
            if (workerId != null) {
                notifyWorker(workerId, orderId);
                log.info("派单成功: orderId={}, workerId={}", orderId, workerId);
            }
            return workerId;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public Long reassign(Long orderId, Long oldWorkerId) {
        if (oldWorkerId != null) {
            String rejectKey = "homeservice:dispatch:reject:" + orderId + ":" + oldWorkerId;
            redisTemplate.opsForValue().set(rejectKey, "1", 300, TimeUnit.SECONDS);
        }
        return dispatch(orderId);
    }

    @Override
    public void cancelDispatch(Long orderId) {
        String key = "homeservice:dispatch:order:" + orderId;
        redisTemplate.delete(key);
        log.info("取消派单: orderId={}", orderId);
    }

    private void notifyWorker(Long workerId, Long orderId) {
        try {
            Result<Void> result = workerFeignClient.notifyDispatch(workerId, orderId);
            if (result.getCode() != 200) {
                log.warn("通知服务人员失败: workerId={}, orderId={}", workerId, orderId);
            }
        } catch (Exception e) {
            log.error("通知服务人员异常: workerId={}, orderId={}, error={}", workerId, orderId, e.getMessage());
        }
    }
}