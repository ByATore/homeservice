package com.homeservice.dispatch.strategy;

import java.util.List;
import java.util.Map;

public interface DispatchStrategy {
    
    List<Map<String, Object>> rank(Map<String, Object> order, List<Map<String, Object>> workers);
    
    int getPriority();
}