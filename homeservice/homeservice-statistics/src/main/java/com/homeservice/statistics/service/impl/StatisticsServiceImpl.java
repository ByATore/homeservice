package com.homeservice.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.homeservice.statistics.entity.Statistics;
import com.homeservice.statistics.mapper.StatisticsMapper;
import com.homeservice.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;
    private final RestTemplate restTemplate;

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> data = new HashMap<>();

        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<Statistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Statistics::getStatDate, today);
        Statistics todayStats = statisticsMapper.selectOne(wrapper);

        if (todayStats != null) {
            data.put("todayOrders", todayStats.getNewOrders());
            data.put("todayAmount", todayStats.getTotalAmount());
        } else {
            data.put("todayOrders", 0);
            data.put("todayAmount", 0);
        }

        LambdaQueryWrapper<Statistics> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.between(Statistics::getStatDate,
                today.withDayOfMonth(1), today);
        List<Statistics> monthStats = statisticsMapper.selectList(monthWrapper);

        int monthOrders = 0;
        double monthAmount = 0;
        for (Statistics s : monthStats) {
            monthOrders += s.getNewOrders() != null ? s.getNewOrders() : 0;
            monthAmount += s.getTotalAmount() != null ? s.getTotalAmount().doubleValue() : 0;
        }
        data.put("monthOrders", monthOrders);
        data.put("monthAmount", monthAmount);

        CompletableFuture<Integer> userFuture = CompletableFuture.supplyAsync(() -> fetchUserCount());
        CompletableFuture<Integer> workerFuture = CompletableFuture.supplyAsync(() -> fetchWorkerCount());

        CompletableFuture.allOf(userFuture, workerFuture).join();

        try {
            data.put("totalUsers", userFuture.get());
        } catch (Exception e) {
            data.put("totalUsers", 0);
        }

        try {
            data.put("totalWorkers", workerFuture.get());
        } catch (Exception e) {
            data.put("totalWorkers", 0);
        }

        data.put("monthIncome", monthAmount);

        return data;
    }

    private Integer fetchUserCount() {
        try {
            Map<String, Object> result = restTemplate.getForObject(
                    "http://homeservice-user/user/count", Map.class);
            if (result != null && "200".equals(String.valueOf(result.get("code")))) {
                Object data = result.get("data");
                if (data instanceof Number) {
                    return ((Number) data).intValue();
                }
            }
        } catch (Exception e) {
            log.warn("获取用户总数失败: {}", e.getMessage());
        }
        return 0;
    }

    private Integer fetchWorkerCount() {
        try {
            Map<String, Object> result = restTemplate.getForObject(
                    "http://homeservice-user/worker/count", Map.class);
            if (result != null && "200".equals(String.valueOf(result.get("code")))) {
                Object data = result.get("data");
                if (data instanceof Number) {
                    return ((Number) data).intValue();
                }
            }
        } catch (Exception e) {
            log.warn("获取服务人员总数失败: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public List<Map<String, Object>> getTopServices(Integer limit) {
        try {
            Map<String, Object> result = restTemplate.getForObject(
                    "http://homeservice-order/order/top-services?limit=" + (limit != null ? limit : 10),
                    Map.class);
            if (result != null && "200".equals(String.valueOf(result.get("code")))) {
                Object data = result.get("data");
                if (data instanceof List) {
                    return (List<Map<String, Object>>) data;
                }
            }
        } catch (Exception e) {
            log.warn("获取热门服务失败: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getTopWorkers(Integer limit) {
        try {
            Map<String, Object> result = restTemplate.getForObject(
                    "http://homeservice-user/worker/top?limit=" + (limit != null ? limit : 10),
                    Map.class);
            if (result != null && "200".equals(String.valueOf(result.get("code")))) {
                Object data = result.get("data");
                if (data instanceof List) {
                    return (List<Map<String, Object>>) data;
                }
            }
        } catch (Exception e) {
            log.warn("获取好评服务人员失败: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getOrderStatusDistribution() {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "http://homeservice-order/order/status-distribution",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            Map<String, Object> body = response.getBody();
            if (body != null && "200".equals(String.valueOf(body.get("code")))) {
                Object data = body.get("data");
                if (data instanceof List) {
                    return (List<Map<String, Object>>) data;
                }
            }
        } catch (Exception e) {
            log.warn("获取订单状态分布失败: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getOrderTrend(String period) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "http://homeservice-order/order/trend?period=" + period,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            Map<String, Object> body = response.getBody();
            if (body != null && "200".equals(String.valueOf(body.get("code")))) {
                Object data = body.get("data");
                if (data instanceof List) {
                    return (List<Map<String, Object>>) data;
                }
            }
        } catch (Exception e) {
            log.warn("获取订单趋势失败: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getRevenueTrend(String period) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "http://homeservice-order/order/revenue-trend?period=" + period,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            Map<String, Object> body = response.getBody();
            if (body != null && "200".equals(String.valueOf(body.get("code")))) {
                Object data = body.get("data");
                if (data instanceof List) {
                    return (List<Map<String, Object>>) data;
                }
            }
        } catch (Exception e) {
            log.warn("获取营收趋势失败: {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}