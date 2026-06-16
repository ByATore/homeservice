package com.homeservice.media.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.media.entity.DecorationComponent;
import com.homeservice.media.service.DecorationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "小程序装修管理")
@RestController
@RequiredArgsConstructor
public class DecorationController {

    private final DecorationService decorationService;

    // ==================== C端公开接口 ====================

    @Operation(summary = "获取页面装修数据")
    @GetMapping("/media/decoration/page")
    public Result<Map<String, Object>> getPageDecoration(@RequestParam(defaultValue = "home") String page) {
        return Result.success(decorationService.getPageDecoration(page));
    }

    // ==================== 管理后台接口 ====================

    @Operation(summary = "分页查询装修组件")
    @GetMapping("/admin/decoration/page")
    public Result<Page<DecorationComponent>> getComponentPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String componentType) {
        return Result.success(decorationService.getComponentPage(current, size, page, componentType));
    }

    @Operation(summary = "新增装修组件")
    @PostMapping("/admin/media/decoration")
    public Result<DecorationComponent> createComponent(@RequestBody DecorationComponent component) {
        component.setStatus(component.getStatus() != null ? component.getStatus() : 1);
        boolean saved = decorationService.save(component);
        return saved ? Result.success(component) : Result.error("创建失败");
    }

    @Operation(summary = "更新装修组件")
    @PutMapping("/admin/decoration/{id}")
    public Result<DecorationComponent> updateComponent(@PathVariable Long id,
                                                        @RequestBody DecorationComponent component) {
        component.setId(id);
        boolean updated = decorationService.updateById(component);
        return updated ? Result.success(component) : Result.error("更新失败");
    }

    @Operation(summary = "删除装修组件")
    @DeleteMapping("/admin/decoration/{id}")
    public Result<Void> deleteComponent(@PathVariable Long id) {
        boolean deleted = decorationService.removeById(id);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "批量更新组件排序")
    @PutMapping("/admin/decoration/sort")
    public Result<Void> updateComponentSort(@RequestBody List<Map<String, Object>> sortList) {
        boolean updated = decorationService.updateSort(sortList);
        return updated ? Result.success() : Result.error("更新失败");
    }

    @Operation(summary = "复制组件")
    @PostMapping("/admin/decoration/{id}/copy")
    public Result<Void> copyComponent(@PathVariable Long id) {
        boolean copied = decorationService.copyComponent(id);
        return copied ? Result.success() : Result.error("复制失败");
    }
}
