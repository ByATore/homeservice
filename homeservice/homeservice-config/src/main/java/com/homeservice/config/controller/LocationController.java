package com.homeservice.config.controller;

import com.homeservice.common.result.Result;
import com.homeservice.config.dto.WorkerLocationDTO;
import com.homeservice.config.service.LocationService;
import com.homeservice.config.vo.AddressVO;
import com.homeservice.config.vo.CityVO;
import com.homeservice.config.vo.WorkerLocationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "定位服务")
@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // ========== 服务人员位置（内部接口） ==========

    @Operation(summary = "更新服务人员位置")
    @PutMapping("/worker/update")
    public Result<Void> updateWorkerLocation(@RequestBody WorkerLocationDTO dto) {
        locationService.updateWorkerLocation(dto.getWorkerId(), dto.getLatitude(), dto.getLongitude());
        return Result.success();
    }

    @Operation(summary = "查询附近服务人员")
    @GetMapping("/worker/nearby")
    public Result<List<WorkerLocationVO>> getNearbyWorkers(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5000") Integer radius) {
        return Result.success(locationService.getNearbyWorkers(latitude, longitude, radius));
    }

    @Operation(summary = "获取服务人员位置")
    @GetMapping("/worker/{workerId}")
    public Result<WorkerLocationVO> getWorkerLocation(@PathVariable Long workerId) {
        WorkerLocationVO location = locationService.getWorkerLocation(workerId);
        return location != null ? Result.success(location) : Result.error("位置信息不存在");
    }

    // ========== C端公开接口 ==========

    @Operation(summary = "逆地理编码：根据经纬度获取地址信息")
    @GetMapping("/geocode/reverse")
    public Result<AddressVO> reverseGeocode(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        return Result.success(locationService.reverseGeocode(latitude, longitude));
    }

    @Operation(summary = "获取当前所在城市")
    @GetMapping("/current-city")
    public Result<CityVO> getCurrentCity(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        return Result.success(locationService.getCurrentCity(latitude, longitude));
    }

    @Operation(summary = "获取热门城市列表")
    @GetMapping("/city/hot")
    public Result<List<CityVO>> getHotCities() {
        return Result.success(locationService.getHotCities());
    }

    @Operation(summary = "获取全部城市列表（按首字母分组）")
    @GetMapping("/city/list")
    public Result<Map<String, List<CityVO>>> getAllCities() {
        return Result.success(locationService.getAllCities());
    }

    @Operation(summary = "搜索城市")
    @GetMapping("/city/search")
    public Result<List<CityVO>> searchCities(@RequestParam String keyword) {
        return Result.success(locationService.searchCities(keyword));
    }
}
