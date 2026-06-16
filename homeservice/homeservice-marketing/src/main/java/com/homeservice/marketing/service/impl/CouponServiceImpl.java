package com.homeservice.marketing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.marketing.entity.Coupon;
import com.homeservice.marketing.mapper.CouponMapper;
import com.homeservice.marketing.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Override
    @Transactional
    public boolean incrementUsedQuantity(Long couponId) {
        Coupon coupon = getById(couponId);
        if (coupon == null) {
            return false;
        }
        coupon.setUsedQuantity(coupon.getUsedQuantity() + 1);
        return updateById(coupon);
    }
}