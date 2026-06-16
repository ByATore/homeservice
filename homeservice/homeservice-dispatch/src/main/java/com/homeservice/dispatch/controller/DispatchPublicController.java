package com.homeservice.dispatch.controller;

import com.homeservice.common.result.Result;
import com.homeservice.dispatch.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "调度派单（管理后台）")
@RestController
@RequestMapping("/dispatch")
@RequiredArgsConstructor
public class DispatchPublicController {

    private final DispatchService dispatchService;

    @Operation(summary = "触发派单")
    @PostMapping("/trigger")
    public Result<Long> triggerDispatch(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Long workerId = dispatchService.dispatch(orderId);
        return workerId != null ? Result.success(workerId) : Result.error("暂无可用服务人员");
    }

    @Operation(summary = "重新派单")
    @PostMapping("/reassign")
    public Result<Long> reassignOrder(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Long oldWorkerId = params.get("oldWorkerId") != null ?
                Long.valueOf(params.get("oldWorkerId").toString()) : null;
        Long workerId = dispatchService.reassign(orderId, oldWorkerId);
        return workerId != null ? Result.success(workerId) : Result.error("暂无可用服务人员");
    }

    @Operation(summary = "取消派单")
    @PostMapping("/cancel")
    public Result<Void> cancelDispatch(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        dispatchService.cancelDispatch(orderId);
        return Result.success();
    }
}