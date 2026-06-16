package com.homeservice.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homeservice.order.entity.Order;
import com.homeservice.order.entity.vo.OrderVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {
    Order getOrderByNo(String orderNo);

    OrderVO getOrderDetailById(Long id);

    OrderVO getOrderDetailByNo(String orderNo);

    IPage<OrderVO> getOrderPageWithDetails(Page<OrderVO> page, String orderNo,
                                            Long userId, Long workerId, Integer status);

    List<OrderVO> getPendingOrders(Integer limit);

    boolean createOrder(Order order);

    boolean updateOrderStatus(Long orderId, Integer status);

    boolean cancelOrder(Long orderId, String reason);

    boolean startService(Long orderId);

    boolean completeService(Long orderId);

    List<Map<String, Object>> getTopServices(Integer limit);

    List<Map<String, Object>> getStatusDistribution();

    List<Map<String, Object>> getOrderTrend(String period);

    List<Map<String, Object>> getRevenueTrend(String period);

    Map<String, Object> getTotalStats();

    // ========== 支付相关 ==========

    /**
     * 支付订单
     */
    boolean payOrder(Long orderId, Integer paymentMethod, String transactionId);

    /**
     * 更新支付状态（用于支付回调）
     */
    boolean updatePayStatus(Long orderId, String paymentNo, String transactionId);

    /**
     * 退款
     */
    boolean refundOrder(Long orderId, BigDecimal refundAmount, String reason);

    /**
     * 根据订单ID查询支付信息
     */
    Map<String, Object> getPaymentByOrderId(Long orderId);

    /**
     * 根据订单号查询支付信息
     */
    Map<String, Object> getPaymentByOrderNo(String orderNo);

    /**
     * 软删除订单
     */
    boolean softDeleteOrder(Long orderId);
}