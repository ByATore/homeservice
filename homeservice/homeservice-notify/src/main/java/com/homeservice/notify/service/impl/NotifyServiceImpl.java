package com.homeservice.notify.service.impl;

import com.homeservice.notify.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    @Override
    public void send(String type, String target, String template, Map<String, String> params) {
        log.info("发送通知: type={}, target={}, template={}, params={}", type, target, template, params);
        
        switch (type.toLowerCase()) {
            case "sms":
                sendSms(target, template, params);
                break;
            case "wechat":
                sendWechatTemplate(target, template, params);
                break;
            case "push":
                sendPush(target, template, params);
                break;
            default:
                log.warn("不支持的通知类型: {}", type);
        }
    }

    @Override
    public void sendSmsCode(String phone, String code) {
        log.info("发送短信验证码: phone={}, code={}", phone, code);
    }

    @Override
    public void sendOrderStatusNotify(String phone, String orderNo, String status) {
        log.info("发送订单状态通知: phone={}, orderNo={}, status={}", phone, orderNo, status);
    }

    @Override
    public void sendDispatchNotify(String phone, String orderNo, String workerName) {
        log.info("发送派单通知: phone={}, orderNo={}, workerName={}", phone, orderNo, workerName);
    }

    @Override
    public void sendPaymentSuccessNotify(String phone, String orderNo, String amount) {
        log.info("发送支付成功通知: phone={}, orderNo={}, amount={}", phone, orderNo, amount);
    }

    private void sendSms(String phone, String template, Map<String, String> params) {
        log.info("发送短信: phone={}, template={}, params={}", phone, template, params);
    }

    private void sendWechatTemplate(String openId, String template, Map<String, String> params) {
        log.info("发送微信模板消息: openId={}, template={}, params={}", openId, template, params);
    }

    private void sendPush(String deviceToken, String template, Map<String, String> params) {
        log.info("发送APP推送: deviceToken={}, template={}, params={}", deviceToken, template, params);
    }
}