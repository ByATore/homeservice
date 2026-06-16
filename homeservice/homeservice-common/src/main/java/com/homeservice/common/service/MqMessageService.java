package com.homeservice.common.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.common.entity.MqMessage;
import com.homeservice.common.mapper.MqMessageMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "spring.datasource.url")
public class MqMessageService extends ServiceImpl<MqMessageMapper, MqMessage> {

    public MqMessage createMessage(String exchange, String routingKey, Object messageBody,
                                    String businessType, String businessId) {
        MqMessage msg = new MqMessage();
        msg.setMessageId(UUID.randomUUID().toString().replace("-", ""));
        msg.setExchange(exchange);
        msg.setRoutingKey(routingKey);
        msg.setMessageBody(JSONUtil.toJsonStr(messageBody));
        msg.setStatus(0);
        msg.setRetryCount(0);
        msg.setMaxRetry(5);
        msg.setNextRetryTime(LocalDateTime.now());
        msg.setBusinessType(businessType);
        msg.setBusinessId(businessId);
        save(msg);
        return msg;
    }

    public void markSuccess(Long messageId) {
        MqMessage msg = new MqMessage();
        msg.setId(messageId);
        msg.setStatus(1);
        updateById(msg);
    }

    public void markFailed(Long messageId, String errorMsg) {
        MqMessage exist = getById(messageId);
        if (exist == null) {
            return;
        }
        int newRetryCount = exist.getRetryCount() + 1;
        MqMessage msg = new MqMessage();
        msg.setId(messageId);
        msg.setRetryCount(newRetryCount);
        msg.setErrorMsg(errorMsg);

        if (newRetryCount >= exist.getMaxRetry()) {
            msg.setStatus(2);
            log.error("消息重试次数已达上限: messageId=" + exist.getMessageId() + ", businessType=" + exist.getBusinessType());
        } else {
            msg.setStatus(0);
            long delaySeconds = (long) Math.pow(2, newRetryCount) * 10;
            msg.setNextRetryTime(LocalDateTime.now().plusSeconds(delaySeconds));
            log.warn("消息发送失败，将在" + delaySeconds + "秒后重试: messageId=" + exist.getMessageId() + ", retryCount=" + newRetryCount);
        }
        updateById(msg);
    }

    public List<MqMessage> getPendingMessages(int limit) {
        return baseMapper.selectPendingMessages(LocalDateTime.now(), limit);
    }
}