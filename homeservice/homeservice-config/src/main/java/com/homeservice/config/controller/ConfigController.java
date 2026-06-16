package com.homeservice.config.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.config.dto.ConfigUpdateRequest;
import com.homeservice.config.entity.SysConfig;
import com.homeservice.config.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "系统配置管理（管理后台）")
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController {

    private final SysConfigService sysConfigService;

    @Operation(summary = "分页查询配置列表")
    @GetMapping("/page")
    public Result<Page<SysConfig>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) String configGroup) {
        return Result.success(sysConfigService.lambdaQuery()
                .eq(configKey != null, SysConfig::getConfigKey, configKey)
                .like(configName != null, SysConfig::getConfigName, configName)
                .eq(configGroup != null, SysConfig::getConfigGroup, configGroup)
                .page(new Page<>(current, size)));
    }

    @Operation(summary = "获取所有配置分组")
    @GetMapping("/groups")
    public Result<Object> getGroups() {
        return Result.success(sysConfigService.list().stream()
                .map(SysConfig::getConfigGroup)
                .distinct()
                .toList());
    }

    @Operation(summary = "根据ID获取配置详情")
    @GetMapping("/{id}")
    public Result<SysConfig> getById(@PathVariable Long id) {
        return Result.success(sysConfigService.getById(id));
    }

    @Operation(summary = "新增配置")
    @PostMapping
    public Result<Void> create(@RequestBody SysConfig sysConfig) {
        sysConfig.setId(null);
        sysConfigService.save(sysConfig);
        return Result.success();
    }

    @Operation(summary = "更新配置值")
    @PutMapping("/{id}")
    public Result<Void> updateValue(@PathVariable Long id, @Valid @RequestBody ConfigUpdateRequest request) {
        boolean success = sysConfigService.updateConfigValue(id, request.getConfigValue());
        return success ? Result.success() : Result.error("配置不存在");
    }

    @Operation(summary = "更新整个配置")
    @PutMapping("/{id}/full")
    public Result<Void> updateFull(@PathVariable Long id, @RequestBody SysConfig sysConfig) {
        sysConfig.setId(id);
        sysConfigService.updateById(sysConfig);
        return Result.success();
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysConfigService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "根据分组获取配置列表")
    @GetMapping("/group/{configGroup}")
    public Result<Map<String, String>> getByGroup(@PathVariable String configGroup) {
        return Result.success(sysConfigService.getConfigMapByGroup(configGroup));
    }
}