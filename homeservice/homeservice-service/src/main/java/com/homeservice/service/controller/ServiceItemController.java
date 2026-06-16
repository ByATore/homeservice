package com.homeservice.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.service.entity.ServiceItem;
import com.homeservice.service.service.ServiceItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "服务项目管理")
@RestController
@RequestMapping("/service/item")
@RequiredArgsConstructor
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    @Operation(summary = "分页查询服务项目")
    @GetMapping("/page")
    public Result<Page<ServiceItem>> getItemPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        
        Page<ServiceItem> page = new Page<>(current, size);
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        
        if (categoryId != null) {
            wrapper.eq(ServiceItem::getCategoryId, categoryId);
        }
        if (name != null && !name.isEmpty()) {
            wrapper.like(ServiceItem::getName, name);
        }
        if (status != null) {
            wrapper.eq(ServiceItem::getStatus, status);
        }
        
        wrapper.orderByAsc(ServiceItem::getSort);
        return Result.success(serviceItemService.page(page, wrapper));
    }

    @Operation(summary = "根据分类ID查询服务项目")
    @GetMapping("/category/{categoryId}")
    public Result<List<ServiceItem>> getItemsByCategoryId(@PathVariable Long categoryId) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceItem::getCategoryId, categoryId);
        wrapper.eq(ServiceItem::getStatus, 1);
        wrapper.orderByAsc(ServiceItem::getSort);
        return Result.success(serviceItemService.list(wrapper));
    }

    @Operation(summary = "根据ID查询服务项目")
    @GetMapping("/{id}")
    public Result<ServiceItem> getItemById(@PathVariable Long id) {
        ServiceItem item = serviceItemService.getById(id);
        if (item == null) {
            return Result.error("服务项目不存在");
        }
        return Result.success(item);
    }

    @Operation(summary = "创建服务项目")
    @PostMapping
    public Result<ServiceItem> createItem(@RequestBody ServiceItem item) {
        boolean saved = serviceItemService.save(item);
        return saved ? Result.success(item) : Result.error("创建失败");
    }

    @Operation(summary = "更新服务项目")
    @PutMapping("/{id}")
    public Result<ServiceItem> updateItem(@PathVariable Long id, @RequestBody ServiceItem item) {
        item.setId(id);
        boolean updated = serviceItemService.updateById(item);
        return updated ? Result.success(item) : Result.error("更新失败");
    }

    @Operation(summary = "删除服务项目")
    @DeleteMapping("/{id}")
    public Result<Void> deleteItem(@PathVariable Long id) {
        boolean deleted = serviceItemService.removeById(id);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "批量删除服务项目")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteItems(@RequestBody List<Long> ids) {
        boolean deleted = serviceItemService.removeByIds(ids);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "搜索服务项目")
    @GetMapping("/search")
    public Result<Page<ServiceItem>> searchItems(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ServiceItem> page = new Page<>(current, size);
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(ServiceItem::getName, keyword)
                .or().like(ServiceItem::getDescription, keyword));
        wrapper.eq(ServiceItem::getStatus, 1);
        wrapper.orderByAsc(ServiceItem::getSort);
        return Result.success(serviceItemService.page(page, wrapper));
    }
}