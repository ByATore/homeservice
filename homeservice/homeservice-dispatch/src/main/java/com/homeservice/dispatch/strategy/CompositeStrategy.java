package com.homeservice.dispatch.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompositeStrategy {
    
    private final GeoPriorityStrategy geoPriorityStrategy;
    private final SkillMatchStrategy skillMatchStrategy;
    
    public List<Map<String, Object>> rank(Map<String, Object> order, List<Map<String, Object>> workers) {
        if (workers.isEmpty()) {
            return workers;
        }
        
        List<Map<String, Object>> result = new ArrayList<>(workers);
        
        result = skillMatchStrategy.rank(order, result);
        result = geoPriorityStrategy.rank(order, result);
        
        return result;
    }
}