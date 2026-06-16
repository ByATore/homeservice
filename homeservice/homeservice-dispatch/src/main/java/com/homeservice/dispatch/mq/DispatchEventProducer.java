package com.homeservice.dispatch.mq;

import com.homeservice.common.constant.RabbitMqConstants;
import com.homeservice.common.entity.MqMessage;
import com.homeservice.common.service.MqMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnBean({RabbitTemplate.class, MqMessageService.class})
public class DispatchEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MqMessageService mqMessageService;

    public void sendDispatchNotify(String phone, String orderNo, String workerName) {
        Map<String, String> message = new HashMap<>();
        message.put("phone", phone);
        message.put("orderNo", orderNo);
        message.put("workerName", workerName);

        MqMessage mqMessage = mqMessageService.createMessage(
                RabbitMqConstants.TOPIC_EXCHANGE,
                RabbitMqConstants.NOTIFY_DISPATCH_KEY,
                message,
                "DISPATCH_NOTIFY",
                orderNo
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMqConstants.TOPIC_EXCHANGE,
                    RabbitMqConstants.NOTIFY_DISPATCH_KEY,
                    message
            );
            mqMessageService.markSuccess(mqMessage.getId());
        } catch (Exception e) {
            mqMessageService.markFailed(mqMessage.getId(), e.getMessage());
        }
    }
}