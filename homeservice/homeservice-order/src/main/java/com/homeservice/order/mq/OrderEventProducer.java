package com.homeservice.order.mq;

import com.homeservice.common.constant.RabbitMqConstants;
import com.homeservice.common.entity.MqMessage;
import com.homeservice.common.service.MqMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MqMessageService mqMessageService;

    public void sendOrderPaidEvent(Long orderId, String orderNo, Long userId) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", orderId);
        message.put("orderNo", orderNo);
        message.put("userId", userId);
        message.put("eventTime", System.currentTimeMillis());

        MqMessage mqMessage = mqMessageService.createMessage(
                RabbitMqConstants.TOPIC_EXCHANGE,
                RabbitMqConstants.ORDER_PAID_KEY,
                message,
                "ORDER_PAID",
                orderNo
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMqConstants.TOPIC_EXCHANGE,
                    RabbitMqConstants.ORDER_PAID_KEY,
                    message
            );
            mqMessageService.markSuccess(mqMessage.getId());
        } catch (Exception e) {
            mqMessageService.markFailed(mqMessage.getId(), e.getMessage());
        }
    }

    public void sendOrderStatusChangedEvent(Long orderId, String orderNo, Integer status, String statusName, String phone) {
        Map<String, String> message = new HashMap<>();
        message.put("orderId", orderId.toString());
        message.put("orderNo", orderNo);
        message.put("status", status.toString());
        message.put("statusName", statusName);
        message.put("phone", phone != null ? phone : "");

        MqMessage mqMessage = mqMessageService.createMessage(
                RabbitMqConstants.TOPIC_EXCHANGE,
                RabbitMqConstants.NOTIFY_ORDER_KEY,
                message,
                "ORDER_STATUS",
                orderNo
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMqConstants.TOPIC_EXCHANGE,
                    RabbitMqConstants.NOTIFY_ORDER_KEY,
                    message
            );
            mqMessageService.markSuccess(mqMessage.getId());
        } catch (Exception e) {
            mqMessageService.markFailed(mqMessage.getId(), e.getMessage());
        }
    }
}