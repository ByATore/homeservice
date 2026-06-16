package com.homeservice.marketing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.marketing.entity.Coupon;
import com.homeservice.marketing.entity.UserCoupon;
import com.homeservice.marketing.mapper.UserCouponMapper;
import com.homeservice.marketing.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "优惠券管理")
@RestController
@RequestMapping("/marketing/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final UserCouponMapper userCouponMapper;

    @Operation(summary = "分页查询优惠券")
    @GetMapping("/page")
    public Result<Page<Coupon>> getCouponPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        
        Page<Coupon> page = new Page<>(current, size);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(Coupon::getName, name);
        }
        if (type != null) {
            wrapper.eq(Coupon::getType, type);
        }
        if (status != null) {
            wrapper.eq(Coupon::getStatus, status);
        }
        
        wrapper.orderByDesc(Coupon::getCreatedAt);
        return Result.success(couponService.page(page, wrapper));
    }

    @Operation(summary = "查询所有有效优惠券")
    @GetMapping("/list")
    public Result<List<Coupon>> getValidCoupons() {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1);
        wrapper.orderByDesc(Coupon::getCreatedAt);
        return Result.success(couponService.list(wrapper));
    }

    @Operation(summary = "根据ID查询优惠券")
    @GetMapping("/{id}")
    public Result<Coupon> getCouponById(@PathVariable Long id) {
        Coupon coupon = couponService.getById(id);
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        return Result.success(coupon);
    }

    @Operation(summary = "创建优惠券")
    @PostMapping
    public Result<Coupon> createCoupon(@RequestBody Coupon coupon) {
        coupon.setUsedQuantity(0);
        boolean saved = couponService.save(coupon);
        return saved ? Result.success(coupon) : Result.error("创建失败");
    }

    @Operation(summary = "更新优惠券")
    @PutMapping("/{id}")
    public Result<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        boolean updated = couponService.updateById(coupon);
        return updated ? Result.success(coupon) : Result.error("更新失败");
    }

    @Operation(summary = "删除优惠券")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCoupon(@PathVariable Long id) {
        boolean deleted = couponService.removeById(id);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "批量删除优惠券")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteCoupons(@RequestBody List<Long> ids) {
        boolean deleted = couponService.removeByIds(ids);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "更新优惠券状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateCouponStatus(@PathVariable Long id, @RequestParam Integer status) {
        Coupon coupon = new Coupon();
        coupon.setId(id);
        coupon.setStatus(status);
        boolean updated = couponService.updateById(coupon);
        return updated ? Result.success() : Result.error("更新失败");
    }

    @Operation(summary = "获取优惠券统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCouponStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", couponService.count());

        LambdaQueryWrapper<Coupon> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(Coupon::getStatus, 1);
        stats.put("activeCount", couponService.count(activeWrapper));

        LambdaQueryWrapper<Coupon> type1Wrapper = new LambdaQueryWrapper<>();
        type1Wrapper.eq(Coupon::getType, 1);
        stats.put("fullReductionCount", couponService.count(type1Wrapper));

        LambdaQueryWrapper<Coupon> type2Wrapper = new LambdaQueryWrapper<>();
        type2Wrapper.eq(Coupon::getType, 2);
        stats.put("discountCount", couponService.count(type2Wrapper));

        return Result.success(stats);
    }

    // ==================== C端用户优惠券接口 ====================

    @Operation(summary = "获取用户可领取的优惠券")
    @GetMapping("/available")
    public Result<List<Coupon>> getAvailableCoupons(@RequestParam Long userId) {
        // 获取所有有效优惠券，排除已领取的
        List<Coupon> allActive = couponService.list(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, 1)
                .le(Coupon::getStartTime, LocalDateTime.now())
                .ge(Coupon::getEndTime, LocalDateTime.now()));

        // 排除已领取且未使用的
        List<Long> receivedIds = userCouponMapper.selectList(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId))
                .stream().map(UserCoupon::getCouponId).collect(Collectors.toList());

        List<Coupon> available = allActive.stream()
                .filter(c -> !receivedIds.contains(c.getId()))
                .collect(Collectors.toList());
        return Result.success(available);
    }

    @Operation(summary = "用户领取优惠券")
    @PostMapping("/{id}/receive")
    public Result<UserCoupon> receiveCoupon(@PathVariable Long id, @RequestParam Long userId) {
        Coupon coupon = couponService.getById(id);
        if (coupon == null || coupon.getStatus() != 1) {
            return Result.error("优惠券不存在或已下架");
        }
        if (coupon.getTotalQuantity() != null && coupon.getUsedQuantity() != null
                && coupon.getUsedQuantity() >= coupon.getTotalQuantity()) {
            return Result.error("优惠券已被抢光");
        }

        // 检查是否已领取
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        wrapper.eq(UserCoupon::getCouponId, id);
        if (userCouponMapper.selectCount(wrapper) > 0) {
            return Result.error("已领取过该优惠券");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(id);
        userCoupon.setStatus(0); // 未使用
        userCoupon.setExpireTime(coupon.getEndTime() != null ? coupon.getEndTime()
                : LocalDateTime.now().plusDays(coupon.getValidDays() != null ? coupon.getValidDays() : 7));
        userCouponMapper.insert(userCoupon);

        // 更新已领取数量
        if (coupon.getUsedQuantity() != null) {
            coupon.setUsedQuantity(coupon.getUsedQuantity() + 1);
        } else {
            coupon.setUsedQuantity(1);
        }
        couponService.updateById(coupon);

        return Result.success(userCoupon);
    }

    @Operation(summary = "获取用户已领取的优惠券")
    @GetMapping("/my")
    public Result<List<Map<String, Object>>> getMyCoupons(@RequestParam Long userId) {
        List<UserCoupon> userCoupons = userCouponMapper.selectList(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .orderByDesc(UserCoupon::getCreatedAt));

        List<Map<String, Object>> result = userCoupons.stream().map(uc -> {
            Coupon coupon = couponService.getById(uc.getCouponId());
            Map<String, Object> item = new HashMap<>();
            item.put("id", uc.getId());
            item.put("userId", uc.getUserId());
            item.put("couponId", uc.getCouponId());
            item.put("status", uc.getStatus());
            item.put("usedTime", uc.getUsedTime());
            item.put("expireTime", uc.getExpireTime());
            item.put("createdAt", uc.getCreatedAt());
            if (coupon != null) {
                item.put("name", coupon.getName());
                item.put("type", coupon.getType());
                item.put("discountAmount", coupon.getDiscountAmount());
                item.put("minAmount", coupon.getMinAmount());
            }
            return item;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    @Operation(summary = "获取用户可用优惠券（未使用+未过期）")
    @GetMapping("/my/available")
    public Result<List<Map<String, Object>>> getMyAvailableCoupons(@RequestParam Long userId) {
        List<UserCoupon> userCoupons = userCouponMapper.selectList(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .eq(UserCoupon::getStatus, 0)
                        .ge(UserCoupon::getExpireTime, LocalDateTime.now())
                        .orderByDesc(UserCoupon::getCreatedAt));

        List<Map<String, Object>> result = userCoupons.stream().map(uc -> {
            Coupon coupon = couponService.getById(uc.getCouponId());
            Map<String, Object> item = new HashMap<>();
            item.put("id", uc.getId());
            item.put("userId", uc.getUserId());
            item.put("couponId", uc.getCouponId());
            item.put("status", uc.getStatus());
            item.put("expireTime", uc.getExpireTime());
            if (coupon != null) {
                item.put("name", coupon.getName());
                item.put("type", coupon.getType());
                item.put("discountAmount", coupon.getDiscountAmount());
                item.put("minAmount", coupon.getMinAmount());
                item.put("description", coupon.getDescription());
            }
            return item;
        }).collect(Collectors.toList());
        return Result.success(result);
    }
}