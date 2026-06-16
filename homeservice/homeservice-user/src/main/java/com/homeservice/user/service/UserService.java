package com.homeservice.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.homeservice.user.entity.User;

public interface UserService extends IService<User> {
    User getUserByUsername(String username);
    User getUserByPhone(String phone);
    boolean updateUserStatus(Long userId, Integer status);
    boolean updateUserBalance(Long userId, java.math.BigDecimal amount);
}