package com.homeservice.media.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.media.entity.Banner;
import com.homeservice.media.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Banner管理")
@RestController
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    // ==================== C端公开接口 ====================

    @Operation(summary = "获取启用中的Banner列表")
    @GetMapping("/media/banner/list")
    public Result<List<Banner>> getActiveBanners(@RequestParam(defaultValue = "home") String position) {
        return Result.success(bannerService.getActiveBanners(position));
    }

    // ==================== 管理后台接口 ====================

    @Operation(summary = "分页查询Banner")
    @GetMapping("/admin/media/banner/page")
    public Result<Page<Banner>> getBannerPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer status) {
        return Result.success(bannerService.getBannerPage(current, size, position, status));
    }

    @Operation(summary = "新增Banner")
    @PostMapping("/admin/media/banner")
    public Result<Banner> createBanner(@RequestBody Banner banner) {
        banner.setStatus(banner.getStatus() != null ? banner.getStatus() : 1);
        boolean saved = bannerService.save(banner);
        return saved ? Result.success(banner) : Result.error("创建失败");
    }

    @Operation(summary = "更新Banner")
    @PutMapping("/admin/banner/{id}")
    public Result<Banner> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        boolean updated = bannerService.updateById(banner);
        return updated ? Result.success(banner) : Result.error("更新失败");
    }

    @Operation(summary = "删除Banner")
    @DeleteMapping("/admin/banner/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        boolean deleted = bannerService.removeById(id);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "更新Banner状态（启用/禁用）")
    @PutMapping("/admin/banner/{id}/status")
    public Result<Void> updateBannerStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean updated = bannerService.updateStatus(id, status);
        return updated ? Result.success() : Result.error("更新失败");
    }

    @Operation(summary = "批量更新Banner排序")
    @PutMapping("/admin/banner/sort")
    public Result<Void> updateBannerSort(@RequestBody List<Map<String, Object>> sortList) {
        boolean updated = bannerService.updateSort(sortList);
        return updated ? Result.success() : Result.error("更新失败");
    }
}
