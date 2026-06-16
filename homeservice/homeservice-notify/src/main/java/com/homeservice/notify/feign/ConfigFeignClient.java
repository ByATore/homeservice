package com.homeservice.notify.feign;

import com.homeservice.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "homeservice-config")
public interface ConfigFeignClient {

    @GetMapping("/public/config/value/{configKey}")
    Result<String> getConfigValue(@PathVariable("configKey") String configKey);
}