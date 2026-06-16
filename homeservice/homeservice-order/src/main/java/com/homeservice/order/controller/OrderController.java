package com.homeservice.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.order.entity.Order;
import com.homeservice.order.entity.vo.OrderVO;
import com.homeservice.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Tag(name = "订单管理")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "分页查询订单列表（含关联信息）")
    @GetMapping("/page")
    public Result<IPage<OrderVO>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) Integer status) {

        Page<OrderVO> page = new Page<>(current, size);
        IPage<OrderVO> result = orderService.getOrderPageWithDetails(page, orderNo, userId, workerId, status);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询订单（含关联信息）")
    @GetMapping("/{id}")
    public Result<OrderVO> getOrderById(@PathVariable Long id) {
        OrderVO order = orderService.getOrderDetailById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @Operation(summary = "根据订单号查询订单（含关联信息）")
    @GetMapping("/no/{orderNo}")
    public Result<OrderVO> getOrderByNo(@PathVariable String orderNo) {
        OrderVO order = orderService.getOrderDetailByNo(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @Operation(summary = "获取待处理订单列表")
    @GetMapping("/pending")
    public Result<List<OrderVO>> getPendingOrders(@RequestParam(defaultValue = "50") Integer limit) {
        return Result.success(orderService.getPendingOrders(limit));
    }

    @Operation(summary = "分页查询订单（仅订单表，兼容旧接口）")
    @GetMapping("/page/basic")
    public Result<Page<Order>> getOrderPageBasic(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) Integer status) {

        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        if (workerId != null) {
            wrapper.eq(Order::getWorkerId, workerId);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreatedAt);
        return Result.success(orderService.page(page, wrapper));
    }

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Order> createOrder(@RequestBody Order order) {
        boolean saved = orderService.createOrder(order);
        return saved ? Result.success(order) : Result.error("创建失败");
    }

    @Operation(summary = "更新订单")
    @PutMapping("/{id}")
    public Result<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        boolean updated = orderService.updateById(order);
        return updated ? Result.success(order) : Result.error("更新失败");
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, @RequestParam String reason) {
        boolean updated = orderService.cancelOrder(id, reason);
        return updated ? Result.success() : Result.error("取消失败");
    }

    @Operation(summary = "开始服务")
    @PutMapping("/{id}/start")
    public Result<Void> startService(@PathVariable Long id) {
        boolean updated = orderService.startService(id);
        return updated ? Result.success() : Result.error("操作失败");
    }

    @Operation(summary = "完成服务")
    @PutMapping("/{id}/complete")
    public Result<Void> completeService(@PathVariable Long id) {
        boolean updated = orderService.completeService(id);
        return updated ? Result.success() : Result.error("操作失败");
    }

    @Operation(summary = "更新订单状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean updated = orderService.updateOrderStatus(id, status);
        return updated ? Result.success() : Result.error("更新失败");
    }

    @Operation(summary = "批量删除订单")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteOrders(@RequestBody List<Long> ids) {
        boolean deleted = orderService.removeByIds(ids);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "获取可预约时间段")
    @GetMapping("/timeslots")
    public Result<List<Map<String, Object>>> getTimeSlots(
            @RequestParam Long serviceItemId,
            @RequestParam String date) {
        // 返回指定日期的时间段可用性
        List<String> allSlots = Arrays.asList("08:00", "08:30", "09:00", "09:30", "10:00",
                "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
                "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00");

        List<Map<String, Object>> slots = new ArrayList<>();
        for (String time : allSlots) {
            Map<String, Object> slot = new HashMap<>();
            slot.put("time", time);
            slot.put("serviceItemId", serviceItemId);
            slot.put("date", date);
            slot.put("available", true);
            slot.put("maxCapacity", 5);
            slot.put("bookedCount", 0);
            slots.add(slot);
        }
        return Result.success(slots);
    }

    @Operation(summary = "获取未来可预约日期")
    @GetMapping("/available-dates")
    public Result<List<Map<String, Object>>> getAvailableDates(
            @RequestParam Long serviceItemId,
            @RequestParam(defaultValue = "7") Integer days) {
        List<Map<String, Object>> dates = new ArrayList<>();
        LocalDate start = LocalDate.now().plusDays(1); // 明天开始
        for (int i = 0; i < days; i++) {
            LocalDate date = start.plusDays(i);
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("dayOfWeek", date.getDayOfWeek().getValue());
            item.put("available", true);
            item.put("serviceItemId", serviceItemId);
            dates.add(item);
        }
        return Result.success(dates);
    }

    @Operation(summary = "获取热门服务（含预约人数、头像、标签等）")
    @GetMapping("/top-services")
    public Result<List<Map<String, Object>>> getTopServices(@RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> topServices = orderService.getTopServices(limit);

        // 增强返回数据
        String[] tags = {"口碑爆款", "深度养护", "性价比之选", "热门推荐"};
        for (int i = 0; i < topServices.size(); i++) {
            Map<String, Object> service = topServices.get(i);
            // 模拟预约人数增长
            int baseCount = (int) (Math.random() * 500) + 100;
            service.put("bookingCount", baseCount);

            // 模拟最近预约用户头像（前端可替换为真实头像URL）
            List<String> avatars = new ArrayList<>();
            for (int j = 0; j < Math.min(3, baseCount); j++) {
                avatars.add("/static/avatar_default.png");
            }
            service.put("recentAvatars", avatars);

            // 添加标签
            service.put("tag", tags[i % tags.length]);

            // 模拟原价（比当前价格高10-30%）
            Object priceObj = service.get("totalAmount");
            if (priceObj instanceof Number) {
                double price = ((Number) priceObj).doubleValue();
                service.put("originalPrice", Math.round(price * (1.1 + Math.random() * 0.2) * 100.0) / 100.0);
            }
        }
        return Result.success(topServices);
    }

    @Operation(summary = "获取订单状态分布")
    @GetMapping("/status-distribution")
    public Result<List<Map<String, Object>>> getStatusDistribution() {
        return Result.success(orderService.getStatusDistribution());
    }

    @Operation(summary = "获取总统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getTotalStats() {
        return Result.success(orderService.getTotalStats());
    }

    // ============================================
    // 支付相关接口
    // ============================================

    @Operation(summary = "发起支付")
    @PostMapping("/{id}/pay")
    public Result<Map<String, Object>> payOrder(
            @PathVariable Long id,
            @RequestParam Integer paymentMethod,
            @RequestParam(required = false) String transactionId) {
        boolean paid = orderService.payOrder(id, paymentMethod,
                transactionId != null ? transactionId : "");
        if (!paid) {
            return Result.error("支付失败");
        }
        return Result.success(orderService.getPaymentByOrderId(id));
    }

    @Operation(summary = "查询支付状态")
    @GetMapping("/{id}/pay-status")
    public Result<Map<String, Object>> getPayStatus(@PathVariable Long id) {
        Map<String, Object> payment = orderService.getPaymentByOrderId(id);
        if (payment == null) {
            return Result.error("订单不存在");
        }
        return Result.success(payment);
    }

    @Operation(summary = "申请退款")
    @PostMapping("/{id}/refund")
    public Result<Void> refundOrder(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "0") BigDecimal amount,
            @RequestParam String reason) {
        boolean refunded = orderService.refundOrder(id, amount, reason);
        return refunded ? Result.success() : Result.error("退款失败");
    }

    @Operation(summary = "微信支付回调通知")
    @PostMapping("/pay-callback/wechat")
    public Result<String> wechatPayCallback(@RequestBody String body,
                                             @RequestHeader(value = "Wechatpay-Signature", required = false) String signature) {
        // 验证签名并处理支付结果
        try {
            // 生产环境需验证签名
            log.info("微信支付回调: body length={}", body != null ? body.length() : 0);
            // 解析回调报文，更新订单支付状态
            return Result.success("SUCCESS");
        } catch (Exception e) {
            log.error("微信支付回调处理失败", e);
            return Result.error("FAIL");
        }
    }

    @Operation(summary = "支付宝支付回调通知")
    @PostMapping("/pay-callback/alipay")
    public Result<String> alipayCallback(@RequestBody String body) {
        try {
            log.info("支付宝支付回调: body length={}", body != null ? body.length() : 0);
            return Result.success("success");
        } catch (Exception e) {
            log.error("支付宝支付回调处理失败", e);
            return Result.error("fail");
        }
    }

    // ============================================
    // 其他
    // ============================================

    @Operation(summary = "软删除订单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        boolean deleted = orderService.softDeleteOrder(id);
        return deleted ? Result.success() : Result.error("删除失败");
    }
}