package com.homeservice.order.mq;

import com.homeservice.common.constant.RabbitMqConstants;
import com.homeservice.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidListener {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitMqConstants.ORDER_PAID_QUEUE)
    public void onOrderPaid(Map<String, Object> message) {
        Long orderId = Long.valueOf(message.get("orderId").toString());
        String orderNo = (String) message.get("orderNo");
        log.info("收到订单支付事件，更新订单状态为已支付: orderId={}, orderNo={}", orderId, orderNo);

        try {
            orderService.updateOrderStatus(orderId, 2);
            log.info("订单状态已更新为已支付: orderId={}", orderId);
        } catch (Exception e) {
            log.error("更新订单状态失败: orderId={}, error={}", orderId, e.getMessage(), e);
        }
    }
}