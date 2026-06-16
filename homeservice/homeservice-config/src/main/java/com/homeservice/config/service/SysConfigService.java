package com.homeservice.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.homeservice.config.entity.SysConfig;

import java.util.List;
import java.util.Map;

public interface SysConfigService extends IService<SysConfig> {

    SysConfig getByConfigKey(String configKey);

    String getConfigValueByKey(String configKey);

    List<SysConfig> listByConfigGroup(String configGroup);

    Map<String, String> getConfigMapByGroup(String configGroup);

    boolean updateConfigValue(String configKey, String configValue);

    boolean updateConfigValue(Long id, String configValue);
}