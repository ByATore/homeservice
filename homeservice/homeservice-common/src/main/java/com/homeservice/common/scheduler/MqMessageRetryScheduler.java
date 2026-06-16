package com.homeservice.common.scheduler;

import cn.hutool.json.JSONUtil;
import com.homeservice.common.entity.MqMessage;
import com.homeservice.common.service.MqMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnBean({RabbitTemplate.class, MqMessageService.class})
public class MqMessageRetryScheduler {

    private final MqMessageService mqMessageService;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 10000)
    public void retryPendingMessages() {
        List<MqMessage> pendingMessages = mqMessageService.getPendingMessages(100);
        for (MqMessage msg : pendingMessages) {
            try {
                rabbitTemplate.convertAndSend(msg.getExchange(), msg.getRoutingKey(), 
                        JSONUtil.parseObj(msg.getMessageBody()));
                mqMessageService.markSuccess(msg.getId());
            } catch (Exception e) {
                mqMessageService.markFailed(msg.getId(), e.getMessage());
            }
        }
    }
}