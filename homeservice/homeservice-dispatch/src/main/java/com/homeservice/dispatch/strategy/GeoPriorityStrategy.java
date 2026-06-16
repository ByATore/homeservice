package com.homeservice.dispatch.strategy;

import com.homeservice.common.util.GeoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeoPriorityStrategy implements DispatchStrategy {
    
    @Override
    public List<Map<String, Object>> rank(Map<String, Object> order, List<Map<String, Object>> workers) {
        double orderLat = Double.parseDouble(order.get("latitude").toString());
        double orderLng = Double.parseDouble(order.get("longitude").toString());
        
        workers.sort(Comparator.comparingDouble(worker -> {
            double workerLat = Double.parseDouble(worker.get("latitude").toString());
            double workerLng = Double.parseDouble(worker.get("longitude").toString());
            return GeoUtil.calculateDistance(orderLat, orderLng, workerLat, workerLng);
        }));
        
        return workers;
    }
    
    @Override
    public int getPriority() {
        return 1;
    }
}