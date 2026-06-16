package com.homeservice.marketing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.homeservice.marketing.entity.Coupon;

public interface CouponService extends IService<Coupon> {
    boolean incrementUsedQuantity(Long couponId);
}