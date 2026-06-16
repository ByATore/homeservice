package com.homeservice.notify.service;

import com.homeservice.common.result.Result;
import com.homeservice.notify.feign.ConfigFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyConfigService {

    private final ConfigFeignClient configFeignClient;

    public String getSmsProvider() {
        return getConfigValue("sms.provider");
    }

    public String getSmsAccessKey() {
        return getConfigValue("sms.access_key");
    }

    public String getSmsSecretKey() {
        return getConfigValue("sms.secret_key");
    }

    public String getSmsSignName() {
        return getConfigValue("sms.sign_name");
    }

    public String getSmsTemplateCode() {
        return getConfigValue("sms.template_code");
    }

    public String getWechatAppId() {
        return getConfigValue("payment.wechat.app_id");
    }

    public String getMapApiKey() {
        return getConfigValue("map.api_key");
    }

    public String getCustomerServicePhone() {
        return getConfigValue("system.customer_service_phone");
    }

    private String getConfigValue(String key) {
        try {
            Result<String> result = configFeignClient.getConfigValue(key);
            if (result != null && result.getCode() == 200) {
                return result.getData();
            }
        } catch (Exception e) {
            log.error("获取配置失败, key={}", key, e);
        }
        return null;
    }
}