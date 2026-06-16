package com.homeservice.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.user.entity.Worker;
import com.homeservice.user.entity.vo.WorkerVO;
import com.homeservice.user.mapper.WorkerMapper;
import com.homeservice.user.service.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "服务人员管理")
@RestController
@RequestMapping("/worker")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;
    private final WorkerMapper workerMapper;

    @Operation(summary = "分页查询服务人员列表（含用户信息）")
    @GetMapping("/page")
    public Result<IPage<WorkerVO>> getWorkerPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String workerNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String specialty) {

        Page<WorkerVO> page = new Page<>(current, size);
        IPage<WorkerVO> result = workerService.getWorkerPageWithUser(page, workerNo, status, specialty);
        return Result.success(result);
    }

    @Operation(summary = "分页查询服务人员（仅worker表，兼容旧接口）")
    @GetMapping("/page/basic")
    public Result<Page<Worker>> getWorkerPageBasic(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String workerNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String specialty) {

        Page<Worker> page = new Page<>(current, size);
        LambdaQueryWrapper<Worker> wrapper = new LambdaQueryWrapper<>();

        if (workerNo != null && !workerNo.isEmpty()) {
            wrapper.like(Worker::getWorkerNo, workerNo);
        }
        if (status != null) {
            wrapper.eq(Worker::getStatus, status);
        }
        if (specialty != null && !specialty.isEmpty()) {
            wrapper.like(Worker::getSpecialty, specialty);
        }

        wrapper.orderByDesc(Worker::getCreatedAt);
        return Result.success(workerService.page(page, wrapper));
    }

    @Operation(summary = "根据ID查询服务人员（含用户信息）")
    @GetMapping("/{id}")
    public Result<WorkerVO> getWorkerById(@PathVariable Long id) {
        WorkerVO worker = workerService.getWorkerDetailById(id);
        if (worker == null) {
            return Result.error("服务人员不存在");
        }
        return Result.success(worker);
    }

    @Operation(summary = "获取可服务人员列表")
    @GetMapping("/available")
    public Result<List<WorkerVO>> getAvailableWorkers(@RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(workerService.getAvailableWorkers(limit));
    }

    @Operation(summary = "根据工号查询服务人员")
    @GetMapping("/no/{workerNo}")
    public Result<Worker> getWorkerByNo(@PathVariable String workerNo) {
        Worker worker = workerService.getWorkerByNo(workerNo);
        if (worker == null) {
            return Result.error("服务人员不存在");
        }
        return Result.success(worker);
    }

    @Operation(summary = "创建服务人员")
    @PostMapping
    public Result<Worker> createWorker(@RequestBody Worker worker) {
        Worker existWorker = workerService.getWorkerByNo(worker.getWorkerNo());
        if (existWorker != null) {
            return Result.error("工号已存在");
        }
        worker.setStatus(1);
        worker.setRating(java.math.BigDecimal.valueOf(5.00));
        worker.setServiceCount(0);
        worker.setTotalEarnings(java.math.BigDecimal.ZERO);
        boolean saved = workerService.save(worker);
        return saved ? Result.success(worker) : Result.error("创建失败");
    }

    @Operation(summary = "更新服务人员")
    @PutMapping("/{id}")
    public Result<Worker> updateWorker(@PathVariable Long id, @RequestBody Worker worker) {
        worker.setId(id);
        boolean updated = workerService.updateById(worker);
        return updated ? Result.success(worker) : Result.error("更新失败");
    }

    @Operation(summary = "删除服务人员")
    @DeleteMapping("/{id}")
    public Result<Void> deleteWorker(@PathVariable Long id) {
        boolean deleted = workerService.removeById(id);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "更新服务人员状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateWorkerStatus(@PathVariable Long id, @RequestParam Integer status) {
        Worker worker = new Worker();
        worker.setId(id);
        worker.setStatus(status);
        boolean updated = workerService.updateById(worker);
        return updated ? Result.success() : Result.error("更新失败");
    }

    @Operation(summary = "批量删除服务人员")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteWorkers(@RequestBody List<Long> ids) {
        boolean deleted = workerService.removeByIds(ids);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "获取服务人员总数")
    @GetMapping("/count")
    public Result<Long> getWorkerCount() {
        return Result.success(workerService.count());
    }

    @Operation(summary = "获取好评服务人员")
    @GetMapping("/top")
    public Result<List<Map<String, Object>>> getTopWorkers(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(workerMapper.selectTopWorkers(limit));
    }
}