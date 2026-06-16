package com.homeservice.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.order.entity.Order;
import com.homeservice.order.entity.vo.OrderVO;
import com.homeservice.order.mapper.OrderMapper;
import com.homeservice.order.mq.OrderEventProducer;
import com.homeservice.order.service.OrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderEventProducer orderEventProducer;

    private static final Map<Integer, String> STATUS_NAME_MAP = new LinkedHashMap<>();

    static {
        STATUS_NAME_MAP.put(1, "待支付");
        STATUS_NAME_MAP.put(2, "待服务");
        STATUS_NAME_MAP.put(3, "服务中");
        STATUS_NAME_MAP.put(4, "待评价");
        STATUS_NAME_MAP.put(5, "已完成");
        STATUS_NAME_MAP.put(6, "已取消");
        STATUS_NAME_MAP.put(7, "已退款");
    }

    @Override
    public Order getOrderByNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return getOne(wrapper);
    }

    @Override
    public OrderVO getOrderDetailById(Long id) {
        return baseMapper.selectOrderDetailById(id);
    }

    @Override
    public OrderVO getOrderDetailByNo(String orderNo) {
        return baseMapper.selectOrderDetailByNo(orderNo);
    }

    @Override
    public IPage<OrderVO> getOrderPageWithDetails(Page<OrderVO> page, String orderNo,
                                                   Long userId, Long workerId, Integer status) {
        return baseMapper.selectOrderPageWithDetails(page, orderNo, userId, workerId, status);
    }

    @Override
    public List<OrderVO> getPendingOrders(Integer limit) {
        return baseMapper.selectPendingOrders(limit);
    }

    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "order-create")
    @Transactional
    public boolean createOrder(Order order) {
        order.setStatus(1);
        order.setDiscountAmount(BigDecimal.ZERO);
        return save(order);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"order:statusDistribution", "order:totalStats"}, allEntries = true)
    public boolean updateOrderStatus(Long orderId, Integer status) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(status);
        boolean updated = updateById(order);

        if (updated) {
            sendStatusChangeNotify(orderId, status);
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, String reason) {
        Order order = getById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(6);
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        boolean updated = updateById(order);

        if (updated) {
            sendStatusChangeNotify(orderId, 6);
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean startService(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(3);
        order.setStartTime(LocalDateTime.now());
        boolean updated = updateById(order);

        if (updated) {
            sendStatusChangeNotify(orderId, 3);
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean completeService(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(4);
        order.setEndTime(LocalDateTime.now());
        boolean updated = updateById(order);

        if (updated) {
            sendStatusChangeNotify(orderId, 4);
        }
        return updated;
    }

    private void sendStatusChangeNotify(Long orderId, Integer status) {
        try {
            Order order = getById(orderId);
            if (order != null) {
                String statusName = STATUS_NAME_MAP.getOrDefault(status, "未知");
                orderEventProducer.sendOrderStatusChangedEvent(
                        orderId, order.getOrderNo(), status, statusName, order.getContactPhone());
            }
        } catch (Exception e) {
            log.error("发送订单状态变更通知失败: orderId=" + orderId + ", status=" + status, e);
        }
    }

    @Override
    @Cacheable(value = "order:topServices", key = "#limit")
    public List<Map<String, Object>> getTopServices(Integer limit) {
        return baseMapper.selectTopServices(limit);
    }

    @Override
    @Cacheable(value = "order:statusDistribution")
    public List<Map<String, Object>> getStatusDistribution() {
        List<Map<String, Object>> statusList = baseMapper.selectStatusDistribution();

        Map<Integer, Long> countMap = new HashMap<>();
        for (Map<String, Object> item : statusList) {
            Integer status = (Integer) item.get("status");
            Long count = ((Number) item.get("count")).longValue();
            countMap.put(status, count);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : STATUS_NAME_MAP.entrySet()) {
            Map<String, Object> statusItem = new HashMap<>();
            statusItem.put("name", entry.getValue());
            statusItem.put("value", countMap.getOrDefault(entry.getKey(), 0L));
            result.add(statusItem);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getOrderTrend(String period) {
        LocalDate now = LocalDate.now();
        LocalDate start;
        DateTimeFormatter labelFmt;
        String dateFormat;

        switch (period) {
            case "week":
                start = now.minusDays(6);
                labelFmt = DateTimeFormatter.ofPattern("MM-dd");
                dateFormat = "%Y-%m-%d";
                break;
            case "year":
                start = now.minusYears(4).withDayOfYear(1);
                labelFmt = DateTimeFormatter.ofPattern("yyyy");
                dateFormat = "%Y";
                break;
            default:
                start = now.minusMonths(11).withDayOfMonth(1);
                labelFmt = DateTimeFormatter.ofPattern("yyyy-MM");
                dateFormat = "%Y-%m";
                break;
        }

        String startDateTime = start.atStartOfDay().toString();
        String endDateTime = now.plusDays(1).atStartOfDay().toString();

        List<Map<String, Object>> trendList = baseMapper.selectOrderTrend(
                dateFormat, startDateTime, endDateTime);

        Map<String, Map<String, Object>> aggregated = new LinkedHashMap<>();
        List<LocalDate> datePoints = new ArrayList<>();
        if ("week".equals(period)) {
            for (int i = 0; i < 7; i++) {
                datePoints.add(start.plusDays(i));
            }
        } else if ("year".equals(period)) {
            for (int i = 0; i < 5; i++) {
                datePoints.add(start.plusYears(i));
            }
        } else {
            for (int i = 0; i < 12; i++) {
                datePoints.add(start.plusMonths(i));
            }
        }

        for (LocalDate dp : datePoints) {
            String key = dp.format(labelFmt);
            Map<String, Object> point = new HashMap<>();
            point.put("label", key);
            point.put("orders", 0L);
            point.put("completed", 0L);
            point.put("amount", BigDecimal.ZERO);
            aggregated.put(key, point);
        }

        for (Map<String, Object> row : trendList) {
            String label = (String) row.get("period_label");
            Integer status = (Integer) row.get("status");
            Long count = ((Number) row.get("order_count")).longValue();
            BigDecimal amount = (BigDecimal) row.get("total_amount");

            Map<String, Object> point = aggregated.get(label);
            if (point != null) {
                point.put("orders", ((Long) point.get("orders")) + count);
                if (status != null && (status == 5 || status == 7)) {
                    point.put("completed", ((Long) point.get("completed")) + count);
                }
                if (amount != null) {
                    point.put("amount", ((BigDecimal) point.get("amount")).add(amount));
                }
            }
        }

        return new ArrayList<>(aggregated.values());
    }

    @Override
    public List<Map<String, Object>> getRevenueTrend(String period) {
        return getOrderTrend(period);
    }

    @Override
    @Cacheable(value = "order:totalStats")
    public Map<String, Object> getTotalStats() {
        return baseMapper.selectTotalStats();
    }

    // ============================================
    // 支付相关
    // ============================================

    @Override
    @Transactional
    public boolean payOrder(Long orderId, Integer paymentMethod, String transactionId) {
        Order order = getById(orderId);
        if (order == null) {
            log.warn(String.format("支付失败：订单不存在, orderId=%s", orderId));
            return false;
        }
        if (order.getStatus() != 1) {
            log.warn(String.format("支付失败：订单状态不允许支付, orderId=%s, status=%s", orderId, order.getStatus()));
            return false;
        }
        order.setPaymentMethod(paymentMethod);
        order.setTransactionId(transactionId);
        order.setPaymentNo("PY" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 6));
        order.setPaidAt(LocalDateTime.now());
        order.setStatus(2); // 待服务
        boolean updated = updateById(order);

        if (updated) {
            sendStatusChangeNotify(orderId, 2);
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean updatePayStatus(Long orderId, String paymentNo, String transactionId) {
        Order order = getById(orderId);
        if (order == null) {
            log.warn(String.format("更新支付状态失败：订单不存在, orderId=%s", orderId));
            return false;
        }
        order.setPaymentNo(paymentNo);
        order.setTransactionId(transactionId);
        order.setPaidAt(LocalDateTime.now());
        order.setStatus(2); // 待服务
        boolean updated = updateById(order);

        if (updated) {
            sendStatusChangeNotify(orderId, 2);
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean refundOrder(Long orderId, BigDecimal refundAmount, String reason) {
        Order order = getById(orderId);
        if (order == null) {
            log.warn(String.format("退款失败：订单不存在, orderId=%s", orderId));
            return false;
        }
        order.setStatus(9); // 已退款
        order.setRefundAmount(refundAmount);
        order.setRefundAt(LocalDateTime.now());
        order.setRefundReason(reason);
        boolean updated = updateById(order);

        if (updated) {
            sendStatusChangeNotify(orderId, 9);
        }
        return updated;
    }

    @Override
    public Map<String, Object> getPaymentByOrderId(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            return null;
        }
        return buildPaymentMap(order);
    }

    @Override
    public Map<String, Object> getPaymentByOrderNo(String orderNo) {
        Order order = getOrderByNo(orderNo);
        if (order == null) {
            return null;
        }
        return buildPaymentMap(order);
    }

    @Override
    @Transactional
    public boolean softDeleteOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(8); // 已删除（软删除）
        return updateById(order);
    }

    private Map<String, Object> buildPaymentMap(Order order) {
        Map<String, Object> payment = new HashMap<>();
        payment.put("orderId", order.getId());
        payment.put("orderNo", order.getOrderNo());
        payment.put("paymentNo", order.getPaymentNo());
        payment.put("paymentMethod", order.getPaymentMethod());
        payment.put("paymentAmount", order.getActualAmount());
        payment.put("transactionId", order.getTransactionId());
        payment.put("status", order.getStatus());
        payment.put("paidAt", order.getPaidAt());
        payment.put("refundAmount", order.getRefundAmount());
        payment.put("refundAt", order.getRefundAt());
        payment.put("refundReason", order.getRefundReason());
        return payment;
    }
}