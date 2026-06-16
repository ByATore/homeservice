package com.homeservice.common.util;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnBean(RedissonClient.class)
public class RedisUtil {
    
    @Autowired
    private RedissonClient redissonClient;
    
    public void set(String key, Object value) {
        redissonClient.getBucket(key).set(value);
    }
    
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redissonClient.getBucket(key).set(value, timeout, unit);
    }
    
    public <T> T get(String key) {
        return (T) redissonClient.getBucket(key).get();
    }
    
    public Boolean delete(String key) {
        return redissonClient.getBucket(key).delete();
    }
    
    public Boolean hasKey(String key) {
        return redissonClient.getBucket(key).isExists();
    }
    
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redissonClient.getBucket(key).expire(timeout, unit);
    }
    
    public Long getExpire(String key) {
        return redissonClient.getBucket(key).remainTimeToLive();
    }
    
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }
    
    public Boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public void unlock(String lockKey) {
        RLock lock = getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}