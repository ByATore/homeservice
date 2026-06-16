package com.homeservice.notify.mq;

import com.homeservice.common.constant.RabbitMqConstants;
import com.homeservice.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyEventListener {

    private final NotifyService notifyService;

    @RabbitListener(queues = RabbitMqConstants.NOTIFY_SMS_QUEUE)
    public void onSmsNotify(Map<String, String> message) {
        String phone = message.get("phone");
        String code = message.get("code");
        log.info("收到短信通知事件: phone={}", phone);
        notifyService.sendSmsCode(phone, code);
    }

    @RabbitListener(queues = RabbitMqConstants.NOTIFY_ORDER_QUEUE)
    public void onOrderNotify(Map<String, String> message) {
        String phone = message.get("phone");
        String orderNo = message.get("orderNo");
        String status = message.get("status");
        String statusName = message.get("statusName");
        log.info("收到订单通知事件: orderNo={}, status={}", orderNo, statusName);
        notifyService.sendOrderStatusNotify(phone, orderNo, statusName != null ? statusName : status);
    }

    @RabbitListener(queues = RabbitMqConstants.NOTIFY_DISPATCH_QUEUE)
    public void onDispatchNotify(Map<String, String> message) {
        String phone = message.get("phone");
        String orderNo = message.get("orderNo");
        String workerName = message.get("workerName");
        log.info("收到派单通知事件: orderNo={}, workerName={}", orderNo, workerName);
        notifyService.sendDispatchNotify(phone, orderNo, workerName);
    }
}