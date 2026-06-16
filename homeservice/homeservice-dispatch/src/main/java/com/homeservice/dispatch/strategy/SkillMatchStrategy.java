package com.homeservice.dispatch.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SkillMatchStrategy implements DispatchStrategy {
    
    @Override
    public List<Map<String, Object>> rank(Map<String, Object> order, List<Map<String, Object>> workers) {
        String requiredSkill = order.get("serviceCategoryId") != null ? 
                order.get("serviceCategoryId").toString() : "";
        
        workers.sort(Comparator.comparingInt(worker -> {
            Object tags = worker.get("serviceTags");
            if (tags instanceof List<?> tagList) {
                return tagList.contains(requiredSkill) ? 0 : 1;
            }
            return 1;
        }));
        
        return workers;
    }
    
    @Override
    public int getPriority() {
        return 2;
    }
}