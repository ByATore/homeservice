package com.homeservice.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.finance.entity.Statistics;
import com.homeservice.finance.mapper.StatisticsMapper;
import com.homeservice.finance.service.FinanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FinanceServiceImpl extends ServiceImpl<StatisticsMapper, Statistics> implements FinanceService {

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        LambdaQueryWrapper<Statistics> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(Statistics::getStatDate, LocalDate.now());
        Statistics todayStats = getOne(todayWrapper);
        
        LambdaQueryWrapper<Statistics> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.between(Statistics::getStatDate, 
                LocalDate.now().withDayOfMonth(1), LocalDate.now());
        List<Statistics> monthStats = list(monthWrapper);
        
        int monthOrders = monthStats.stream().mapToInt(Statistics::getNewOrders).sum();
        BigDecimal monthAmount = monthStats.stream()
                .map(s -> s.getTotalAmount() != null ? s.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal monthIncome = monthStats.stream()
                .map(s -> s.getTotalIncome() != null ? s.getTotalIncome() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        overview.put("todayOrders", todayStats != null ? todayStats.getNewOrders() : 0);
        overview.put("todayAmount", todayStats != null ? todayStats.getTotalAmount() : BigDecimal.ZERO);
        overview.put("monthOrders", monthOrders);
        overview.put("monthAmount", monthAmount);
        overview.put("monthIncome", monthIncome);
        overview.put("totalUsers", todayStats != null ? todayStats.getTotalUsers() : 0);
        overview.put("totalWorkers", todayStats != null ? todayStats.getTotalWorkers() : 0);
        
        return overview;
    }

    @Override
    public Map<String, Object> getIncomeStats(String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        LambdaQueryWrapper<Statistics> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(Statistics::getStatDate, LocalDate.parse(startDate));
        }
        if (endDate != null) {
            wrapper.le(Statistics::getStatDate, LocalDate.parse(endDate));
        }
        wrapper.orderByAsc(Statistics::getStatDate);
        
        List<Statistics> list = list(wrapper);
        
        List<String> dates = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        List<BigDecimal> incomes = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();
        
        for (Statistics s : list) {
            dates.add(s.getStatDate().toString());
            amounts.add(s.getTotalAmount() != null ? s.getTotalAmount() : BigDecimal.ZERO);
            incomes.add(s.getTotalIncome() != null ? s.getTotalIncome() : BigDecimal.ZERO);
            orders.add(s.getCompletedOrders());
        }
        
        stats.put("dates", dates);
        stats.put("amounts", amounts);
        stats.put("incomes", incomes);
        stats.put("orders", orders);
        
        return stats;
    }

    @Override
    public Map<String, Object> getOrderIncomeStats(String startDate, String endDate) {
        return getIncomeStats(startDate, endDate);
    }

    @Override
    public Page<Map<String, Object>> getSettlementList(Integer current, Integer size, String startDate, String endDate, Long workerId) {
        Page<Map<String, Object>> page = new Page<>(current, size);
        List<Map<String, Object>> records = new ArrayList<>();
        
        Map<String, Object> record = new HashMap<>();
        record.put("workerId", 1L);
        record.put("workerName", "示例服务人员");
        record.put("orderCount", 10);
        record.put("serviceAmount", new BigDecimal("5000.00"));
        record.put("platformFee", new BigDecimal("500.00"));
        record.put("settlementAmount", new BigDecimal("4500.00"));
        record.put("settlementStatus", "待结算");
        records.add(record);
        
        page.setRecords(records);
        page.setTotal(1);
        return page;
    }

    @Override
    public Page<Statistics> getStatisticsPage(Integer current, Integer size, String startDate, String endDate) {
        Page<Statistics> page = new Page<>(current, size);
        LambdaQueryWrapper<Statistics> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(Statistics::getStatDate, LocalDate.parse(startDate));
        }
        if (endDate != null) {
            wrapper.le(Statistics::getStatDate, LocalDate.parse(endDate));
        }
        wrapper.orderByDesc(Statistics::getStatDate);
        return page(page, wrapper);
    }

    @Override
    public Map<String, Object> getReconciliationReport(String date) {
        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("totalOrders", 100);
        report.put("totalAmount", new BigDecimal("50000.00"));
        report.put("platformIncome", new BigDecimal("5000.00"));
        report.put("workerSettlement", new BigDecimal("45000.00"));
        report.put("status", "已对账");
        return report;
    }

    @Override
    public Map<String, Object> getTaxReport(String startDate, String endDate) {
        Map<String, Object> report = new HashMap<>();
        report.put("period", startDate + " ~ " + endDate);
        report.put("totalRevenue", new BigDecimal("100000.00"));
        report.put("taxableAmount", new BigDecimal("100000.00"));
        report.put("taxRate", "6%");
        report.put("taxAmount", new BigDecimal("6000.00"));
        return report;
    }
}