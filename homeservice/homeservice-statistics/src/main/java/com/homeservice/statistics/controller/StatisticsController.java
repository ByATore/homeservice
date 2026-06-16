package com.homeservice.statistics.controller;

import com.homeservice.common.result.Result;
import com.homeservice.statistics.feign.OrderFeignClient;
import com.homeservice.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "数据统计")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final OrderFeignClient orderFeignClient;

    @Operation(summary = "获取Dashboard数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        return Result.success(statisticsService.getDashboard());
    }

    @Operation(summary = "获取好评服务人员")
    @GetMapping("/top-workers")
    public Result<List<Map<String, Object>>> getTopWorkers(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(statisticsService.getTopWorkers(limit));
    }

    @Operation(summary = "获取订单状态分布（通过 Feign 调用订单服务）")
    @GetMapping("/order-status")
    public Result<List<Map<String, Object>>> getOrderStatusDistribution() {
        return orderFeignClient.getStatusDistribution();
    }

    @Operation(summary = "获取订单趋势")
    @GetMapping("/order-trend")
    public Result<List<Map<String, Object>>> getOrderTrend(@RequestParam(defaultValue = "month") String period) {
        return Result.success(statisticsService.getOrderTrend(period));
    }
}