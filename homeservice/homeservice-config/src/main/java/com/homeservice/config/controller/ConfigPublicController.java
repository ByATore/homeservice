package com.homeservice.config.controller;

import com.homeservice.common.result.Result;
import com.homeservice.config.entity.SysConfig;
import com.homeservice.config.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "系统配置（公共服务）")
@RestController
@RequestMapping("/public/config")
@RequiredArgsConstructor
public class ConfigPublicController {

    private final SysConfigService sysConfigService;

    @Operation(summary = "根据配置键获取配置值")
    @GetMapping("/value/{configKey}")
    public Result<String> getConfigValue(@PathVariable String configKey) {
        String value = sysConfigService.getConfigValueByKey(configKey);
        if (value == null) {
            return Result.error("配置不存在");
        }
        return Result.success(value);
    }

    @Operation(summary = "根据配置键获取完整配置信息")
    @GetMapping("/{configKey}")
    public Result<SysConfig> getConfig(@PathVariable String configKey) {
        SysConfig config = sysConfigService.getByConfigKey(configKey);
        if (config == null) {
            return Result.error("配置不存在");
        }
        return Result.success(config);
    }

    @Operation(summary = "根据分组获取所有配置")
    @GetMapping("/group/{configGroup}")
    public Result<Map<String, String>> getConfigByGroup(@PathVariable String configGroup) {
        return Result.success(sysConfigService.getConfigMapByGroup(configGroup));
    }

    @Operation(summary = "批量获取配置值")
    @GetMapping("/batch")
    public Result<Map<String, String>> getConfigBatch(@RequestParam List<String> keys) {
        Map<String, String> resultMap = new java.util.HashMap<>();
        for (String key : keys) {
            String value = sysConfigService.getConfigValueByKey(key);
            if (value != null) {
                resultMap.put(key, value);
            }
        }
        return Result.success(resultMap);
    }
}