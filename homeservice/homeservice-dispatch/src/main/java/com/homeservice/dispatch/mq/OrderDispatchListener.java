package com.homeservice.dispatch.mq;

import com.homeservice.common.constant.RabbitMqConstants;
import com.homeservice.dispatch.service.DispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDispatchListener {

    private final DispatchService dispatchService;

    @RabbitListener(queues = RabbitMqConstants.ORDER_PAID_QUEUE)
    public void onOrderPaid(Map<String, Object> message) {
        Long orderId = Long.valueOf(message.get("orderId").toString());
        log.info("收到订单支付事件，开始派单: orderId={}", orderId);

        try {
            Long workerId = dispatchService.dispatch(orderId);
            if (workerId != null) {
                log.info("派单成功: orderId={}, workerId={}", orderId, workerId);
            } else {
                log.warn("派单失败，暂无可用服务人员: orderId={}", orderId);
            }
        } catch (Exception e) {
            log.error("派单异常: orderId={}, error={}", orderId, e.getMessage(), e);
        }
    }
}