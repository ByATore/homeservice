package com.homeservice.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.finance.entity.Statistics;
import com.homeservice.finance.service.FinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "财务管理（管理后台）")
@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @Operation(summary = "获取财务概览")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        return Result.success(financeService.getOverview());
    }

    @Operation(summary = "获取收入统计（支持日期范围或趋势周期）")
    @GetMapping("/income")
    public Result<Map<String, Object>> getIncomeStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "month") String period) {
        Map<String, Object> result = financeService.getIncomeStats(startDate, endDate);
        result.put("period", period);
        return Result.success(result);
    }

    @Operation(summary = "获取收入趋势（按 period 聚合）")
    @GetMapping("/income/trend")
    public Result<Map<String, Object>> getRevenueTrend(
            @RequestParam(defaultValue = "month") String period) {
        return Result.success(financeService.getIncomeStats(null, null));
    }

    @Operation(summary = "获取订单收入统计")
    @GetMapping("/order-income")
    public Result<Map<String, Object>> getOrderIncomeStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(financeService.getOrderIncomeStats(startDate, endDate));
    }

    @Operation(summary = "获取服务人员结算列表")
    @GetMapping("/settlement")
    public Result<Page<Map<String, Object>>> getSettlementList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long workerId) {
        return Result.success(financeService.getSettlementList(current, size, startDate, endDate, workerId));
    }

    @Operation(summary = "获取统计数据")
    @GetMapping("/statistics")
    public Result<Page<Statistics>> getStatisticsPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(financeService.getStatisticsPage(current, size, startDate, endDate));
    }

    @Operation(summary = "获取对账报表")
    @GetMapping("/reconciliation")
    public Result<Map<String, Object>> getReconciliationReport(
            @RequestParam String date) {
        return Result.success(financeService.getReconciliationReport(date));
    }

    @Operation(summary = "获取税务报表")
    @GetMapping("/tax-report")
    public Result<Map<String, Object>> getTaxReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return Result.success(financeService.getTaxReport(startDate, endDate));
    }
}