package com.homeservice.statistics.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    
    Map<String, Object> getDashboard();
    
    List<Map<String, Object>> getTopServices(Integer limit);

    List<Map<String, Object>> getTopWorkers(Integer limit);

    List<Map<String, Object>> getOrderStatusDistribution();

    List<Map<String, Object>> getOrderTrend(String period);

    List<Map<String, Object>> getRevenueTrend(String period);
}