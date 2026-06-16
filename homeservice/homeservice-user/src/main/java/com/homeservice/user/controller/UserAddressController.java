package com.homeservice.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.homeservice.common.result.Result;
import com.homeservice.user.entity.UserAddress;
import com.homeservice.user.mapper.UserAddressMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户地址管理")
@RestController
@RequestMapping("/user/address")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressMapper userAddressMapper;

    @Operation(summary = "获取当前用户地址列表")
    @GetMapping("/list")
    public Result<List<UserAddress>> getAddressList(@RequestParam Long userId) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId);
        wrapper.orderByDesc(UserAddress::getIsDefault);
        wrapper.orderByDesc(UserAddress::getUpdatedAt);
        return Result.success(userAddressMapper.selectList(wrapper));
    }

    @Operation(summary = "新增地址")
    @PostMapping
    public Result<UserAddress> createAddress(@RequestBody UserAddress address) {
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        userAddressMapper.insert(address);
        return Result.success(address);
    }

    @Operation(summary = "更新地址")
    @PutMapping("/{id}")
    public Result<UserAddress> updateAddress(@PathVariable Long id, @RequestBody UserAddress address) {
        address.setId(id);
        userAddressMapper.updateById(address);
        return Result.success(address);
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        userAddressMapper.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "设为默认地址")
    @PutMapping("/{id}/default")
    @Transactional
    public Result<Void> setDefault(@PathVariable Long id, @RequestParam Long userId) {
        // 先取消其他默认地址
        UserAddress update = new UserAddress();
        update.setIsDefault(0);
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId);
        userAddressMapper.update(update, wrapper);

        // 设置为默认
        UserAddress address = new UserAddress();
        address.setId(id);
        address.setIsDefault(1);
        userAddressMapper.updateById(address);
        return Result.success();
    }
}
