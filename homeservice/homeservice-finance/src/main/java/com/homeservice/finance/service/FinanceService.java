package com.homeservice.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homeservice.finance.entity.Statistics;

import java.math.BigDecimal;
import java.util.Map;

public interface FinanceService extends IService<Statistics> {
    
    Map<String, Object> getOverview();
    
    Map<String, Object> getIncomeStats(String startDate, String endDate);
    
    Map<String, Object> getOrderIncomeStats(String startDate, String endDate);
    
    Page<Map<String, Object>> getSettlementList(Integer current, Integer size, String startDate, String endDate, Long workerId);
    
    Page<Statistics> getStatisticsPage(Integer current, Integer size, String startDate, String endDate);
    
    Map<String, Object> getReconciliationReport(String date);
    
    Map<String, Object> getTaxReport(String startDate, String endDate);
}