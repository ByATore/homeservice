package com.homeservice.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.config.entity.SysConfig;
import com.homeservice.config.mapper.SysConfigMapper;
import com.homeservice.config.service.SysConfigService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public SysConfig getByConfigKey(String configKey) {
        return this.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey)
                .eq(SysConfig::getStatus, 1));
    }

    @Override
    public String getConfigValueByKey(String configKey) {
        SysConfig config = getByConfigKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public List<SysConfig> listByConfigGroup(String configGroup) {
        return this.list(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigGroup, configGroup)
                .eq(SysConfig::getStatus, 1));
    }

    @Override
    public Map<String, String> getConfigMapByGroup(String configGroup) {
        List<SysConfig> configs = listByConfigGroup(configGroup);
        return configs.stream()
                .collect(Collectors.toMap(SysConfig::getConfigKey, SysConfig::getConfigValue));
    }

    @Override
    public boolean updateConfigValue(String configKey, String configValue) {
        SysConfig config = getByConfigKey(configKey);
        if (config == null) {
            return false;
        }
        config.setConfigValue(configValue);
        return this.updateById(config);
    }

    @Override
    public boolean updateConfigValue(Long id, String configValue) {
        SysConfig config = new SysConfig();
        config.setId(id);
        config.setConfigValue(configValue);
        return this.updateById(config);
    }
}