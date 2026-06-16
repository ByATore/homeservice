package com.homeservice.dispatch.service;

public interface DispatchService {
    
    Long dispatch(Long orderId);
    
    Long reassign(Long orderId, Long oldWorkerId);
    
    void cancelDispatch(Long orderId);
}