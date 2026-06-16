package com.homeservice.dispatch.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkerFeignClientFallbackFactory implements FallbackFactory<WorkerFeignClient> {
    
    @Override
    public WorkerFeignClient create(Throwable cause) {
        log.error("WorkerFeignClient 调用失败: {}", cause.getMessage());
        return new WorkerFeignClient() {
            @Override
            public com.homeservice.common.result.Result<java.util.List<java.util.Map<String, Object>>> getAvailableWorkers(String cityCode) {
                return com.homeservice.common.result.Result.error("服务人员服务不可用");
            }
            
            @Override
            public com.homeservice.common.result.Result<java.util.Map<String, Object>> getWorkerById(Long id) {
                return com.homeservice.common.result.Result.error("服务人员服务不可用");
            }
            
            @Override
            public com.homeservice.common.result.Result<Void> notifyDispatch(Long id, Long orderId) {
                return com.homeservice.common.result.Result.error("服务人员服务不可用");
            }
        };
    }
}