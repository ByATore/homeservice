package com.homeservice.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.homeservice.common.result.Result;
import com.homeservice.user.entity.UserFavorite;
import com.homeservice.user.mapper.UserFavoriteMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "用户收藏管理")
@RestController
@RequestMapping("/user/favorite")
@RequiredArgsConstructor
public class UserFavoriteController {

    private final UserFavoriteMapper userFavoriteMapper;

    @Operation(summary = "获取收藏列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getFavoriteList(@RequestParam Long userId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId);
        wrapper.orderByDesc(UserFavorite::getCreatedAt);
        List<UserFavorite> favorites = userFavoriteMapper.selectList(wrapper);

        List<Map<String, Object>> result = favorites.stream().map(f -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", f.getId());
            item.put("userId", f.getUserId());
            item.put("serviceItemId", f.getServiceItemId());
            item.put("createdAt", f.getCreatedAt());
            return item;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    @Operation(summary = "收藏服务")
    @PostMapping
    public Result<UserFavorite> addFavorite(@RequestBody UserFavorite favorite) {
        // 检查是否已收藏
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, favorite.getUserId());
        wrapper.eq(UserFavorite::getServiceItemId, favorite.getServiceItemId());
        UserFavorite exist = userFavoriteMapper.selectOne(wrapper);
        if (exist != null) {
            return Result.error("已收藏该服务");
        }
        userFavoriteMapper.insert(favorite);
        return Result.success(favorite);
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{serviceItemId}")
    public Result<Void> removeFavorite(@PathVariable Long serviceItemId, @RequestParam Long userId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId);
        wrapper.eq(UserFavorite::getServiceItemId, serviceItemId);
        userFavoriteMapper.delete(wrapper);
        return Result.success();
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check/{serviceItemId}")
    public Result<Map<String, Object>> checkFavorite(@PathVariable Long serviceItemId, @RequestParam Long userId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId);
        wrapper.eq(UserFavorite::getServiceItemId, serviceItemId);
        UserFavorite exist = userFavoriteMapper.selectOne(wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("isFavorite", exist != null);
        return Result.success(result);
    }
}
