package com.homeservice.config.service;

import com.homeservice.config.vo.AddressVO;
import com.homeservice.config.vo.CityVO;
import com.homeservice.config.vo.WorkerLocationVO;

import java.util.List;
import java.util.Map;

/**
 * 定位服务接口
 */
public interface LocationService {

    // ========== 服务人员位置（内部接口） ==========

    /**
     * 更新服务人员位置
     */
    void updateWorkerLocation(Long workerId, Double latitude, Double longitude);

    /**
     * 查询附近服务人员
     */
    List<WorkerLocationVO> getNearbyWorkers(Double latitude, Double longitude, Integer radius);

    /**
     * 获取单个服务人员位置
     */
    WorkerLocationVO getWorkerLocation(Long workerId);

    // ========== C端公开接口 ==========

    /**
     * 逆地理编码：根据经纬度获取地址信息
     */
    AddressVO reverseGeocode(Double latitude, Double longitude);

    /**
     * 获取当前所在城市
     */
    CityVO getCurrentCity(Double latitude, Double longitude);

    /**
     * 获取热门城市列表
     */
    List<CityVO> getHotCities();

    /**
     * 获取全部城市列表（按首字母分组）
     */
    Map<String, List<CityVO>> getAllCities();

    /**
     * 搜索城市
     */
    List<CityVO> searchCities(String keyword);
}
