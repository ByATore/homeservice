package com.homeservice.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.user.entity.User;
import com.homeservice.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<Page<User>> getUserPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer status) {
        
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(User::getUsername, username);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(User::getPhone, phone);
        }
        if (userType != null) {
            wrapper.eq(User::getUserType, userType);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreatedAt);
        return Result.success(userService.page(page, wrapper));
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        User existUser = userService.getUserByUsername(user.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        if (user.getPhone() != null) {
            existUser = userService.getUserByPhone(user.getPhone());
            if (existUser != null) {
                return Result.error("手机号已存在");
            }
        }
        user.setStatus(1);
        user.setBalance(java.math.BigDecimal.ZERO);
        boolean saved = userService.save(user);
        return saved ? Result.success(user) : Result.error("创建失败");
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean updated = userService.updateById(user);
        return updated ? Result.success(user) : Result.error("更新失败");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.removeById(id);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "根据用户名查询用户")
    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean updated = userService.updateUserStatus(id, status);
        return updated ? Result.success() : Result.error("更新失败");
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteUsers(@RequestBody List<Long> ids) {
        boolean deleted = userService.removeByIds(ids);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "获取用户总数")
    @GetMapping("/count")
    public Result<Long> getUserCount() {
        return Result.success(userService.count());
    }

    @Operation(summary = "根据openid查询用户")
    @GetMapping("/openid/{openid}")
    public Result<User> getUserByOpenid(@PathVariable String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        User user = userService.getOne(wrapper);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }
}