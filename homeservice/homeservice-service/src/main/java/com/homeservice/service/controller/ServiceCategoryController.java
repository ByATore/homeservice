package com.homeservice.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.service.entity.ServiceCategory;
import com.homeservice.service.service.ServiceCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "服务分类管理")
@RestController
@RequestMapping("/service/category")
@RequiredArgsConstructor
public class ServiceCategoryController {

    private final ServiceCategoryService serviceCategoryService;

    @Operation(summary = "分页查询服务分类")
    @GetMapping("/page")
    public Result<Page<ServiceCategory>> getCategoryPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        
        Page<ServiceCategory> page = new Page<>(current, size);
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(ServiceCategory::getName, name);
        }
        if (status != null) {
            wrapper.eq(ServiceCategory::getStatus, status);
        }
        
        wrapper.orderByAsc(ServiceCategory::getSort);
        return Result.success(serviceCategoryService.page(page, wrapper));
    }

    @Operation(summary = "查询所有服务分类")
    @GetMapping("/list")
    public Result<List<ServiceCategory>> getCategoryList(@RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(ServiceCategory::getStatus, status);
        }
        wrapper.orderByAsc(ServiceCategory::getSort);
        return Result.success(serviceCategoryService.list(wrapper));
    }

    @Operation(summary = "根据ID查询服务分类")
    @GetMapping("/{id}")
    public Result<ServiceCategory> getCategoryById(@PathVariable Long id) {
        ServiceCategory category = serviceCategoryService.getById(id);
        if (category == null) {
            return Result.error("服务分类不存在");
        }
        return Result.success(category);
    }

    @Operation(summary = "创建服务分类")
    @PostMapping
    public Result<ServiceCategory> createCategory(@RequestBody ServiceCategory category) {
        boolean saved = serviceCategoryService.save(category);
        return saved ? Result.success(category) : Result.error("创建失败");
    }

    @Operation(summary = "更新服务分类")
    @PutMapping("/{id}")
    public Result<ServiceCategory> updateCategory(@PathVariable Long id, @RequestBody ServiceCategory category) {
        category.setId(id);
        boolean updated = serviceCategoryService.updateById(category);
        return updated ? Result.success(category) : Result.error("更新失败");
    }

    @Operation(summary = "删除服务分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        boolean deleted = serviceCategoryService.removeById(id);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "批量删除服务分类")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteCategories(@RequestBody List<Long> ids) {
        boolean deleted = serviceCategoryService.removeByIds(ids);
        return deleted ? Result.success() : Result.error("删除失败");
    }
}