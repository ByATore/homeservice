package com.homeservice.config.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.homeservice.common.util.GeoUtil;
import com.homeservice.config.entity.SysConfig;
import com.homeservice.config.service.LocationService;
import com.homeservice.config.service.SysConfigService;
import com.homeservice.config.vo.AddressVO;
import com.homeservice.config.vo.CityVO;
import com.homeservice.config.vo.WorkerLocationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 定位服务实现
 * <p>
 * 使用 Redis GEO 数据结构存储和查询服务人员位置，替代原有的 KEYS * 遍历方式。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private static final String WORKER_LOCATION_PREFIX = "homeservice:worker:location:";
    private static final String WORKER_GEO_KEY = "homeservice:worker:geo";

    private final RedisTemplate<String, Object> redisTemplate;
    private final SysConfigService sysConfigService;

    @Override
    public void updateWorkerLocation(Long workerId, Double latitude, Double longitude) {
        // 1. 存储详细位置信息
        String key = WORKER_LOCATION_PREFIX + workerId;
        WorkerLocationVO loc = WorkerLocationVO.builder()
                .workerId(workerId)
                .latitude(latitude)
                .longitude(longitude)
                .updateTime(System.currentTimeMillis())
                .build();
        redisTemplate.opsForValue().set(key, loc, 30, TimeUnit.MINUTES);

        // 2. 使用 GEO 数据结构存储（优化：替代 ZSet 手动 geohash）
        Point point = new Point(longitude, latitude);
        redisTemplate.opsForGeo().add(WORKER_GEO_KEY, point, workerId.toString());

        log.debug("更新服务人员位置: workerId={}, lat={}, lng={}", workerId, latitude, longitude);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<WorkerLocationVO> getNearbyWorkers(Double latitude, Double longitude, Integer radius) {
        List<WorkerLocationVO> result = new ArrayList<>();

        try {
            // 使用 Redis GEORADIUS 替代 KEYS * 遍历
            Circle circle = new Circle(new Point(longitude, latitude),
                    new Distance(radius, Metrics.METERS));
            RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                    .newGeoRadiusArgs()
                    .includeDistance()
                    .includeCoordinates()
                    .sortAscending();

            GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults =
                    redisTemplate.opsForGeo().radius(WORKER_GEO_KEY, circle, args);

            if (geoResults != null) {
                for (GeoResult<RedisGeoCommands.GeoLocation<Object>> geoResult : geoResults) {
                    RedisGeoCommands.GeoLocation<Object> geoLocation = geoResult.getContent();
                    String workerIdStr = geoLocation.getName().toString();
                    Long workerId = Long.valueOf(workerIdStr);

                    // 获取详细位置信息
                    String key = WORKER_LOCATION_PREFIX + workerId;
                    Object cached = redisTemplate.opsForValue().get(key);
                    if (cached instanceof WorkerLocationVO) {
                        WorkerLocationVO loc = (WorkerLocationVO) cached;
                        loc.setDistance(geoResult.getDistance().getValue());
                        loc.setDistanceText(GeoUtil.formatDistance(geoResult.getDistance().getValue()));
                        result.add(loc);
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询附近服务人员异常: {}", e.getMessage(), e);
            // 降级：使用原有的 KEYS 方式
            return getNearbyWorkersFallback(latitude, longitude, radius);
        }

        return result;
    }

    /**
     * 降级方案：当 GEO 查询异常时使用原有的 KEYS 遍历方式
     */
    private List<WorkerLocationVO> getNearbyWorkersFallback(Double latitude, Double longitude, Integer radius) {
        List<WorkerLocationVO> result = new ArrayList<>();
        try {
            Set<String> keys = redisTemplate.keys(WORKER_LOCATION_PREFIX + "*");
            if (keys != null) {
                for (String key : keys) {
                    Object value = redisTemplate.opsForValue().get(key);
                    if (value instanceof WorkerLocationVO) {
                        WorkerLocationVO loc = (WorkerLocationVO) value;
                        double distance = GeoUtil.calculateDistance(
                                latitude, longitude, loc.getLatitude(), loc.getLongitude());
                        if (distance <= radius) {
                            loc.setDistance(distance);
                            loc.setDistanceText(GeoUtil.formatDistance(distance));
                            result.add(loc);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("降级查询附近服务人员异常: {}", e.getMessage());
        }
        result.sort(Comparator.comparingDouble(WorkerLocationVO::getDistance));
        return result;
    }

    @Override
    public WorkerLocationVO getWorkerLocation(Long workerId) {
        String key = WORKER_LOCATION_PREFIX + workerId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof WorkerLocationVO) {
            return (WorkerLocationVO) value;
        }
        return null;
    }

    @Override
    public AddressVO reverseGeocode(Double latitude, Double longitude) {
        String mapKey = getConfigValue("map.tencent.key", "");
        if (StrUtil.isBlank(mapKey)) {
            log.warn("腾讯地图 Key 未配置");
            return AddressVO.builder()
                    .latitude(latitude).longitude(longitude)
                    .address("未知位置")
                    .build();
        }

        try {
            String url = StrUtil.format(
                    "https://apis.map.qq.com/ws/geocoder/v1/?location={},{}&key={}&get_poi=0",
                    latitude, longitude, mapKey);
            String response = HttpUtil.get(url);
            JSONObject json = JSONUtil.parseObj(response);

            if (json.getInt("status") == 0) {
                JSONObject result = json.getJSONObject("result");
                JSONObject addressComponent = result.getJSONObject("address_component");
                return AddressVO.builder()
                        .province(addressComponent.getStr("province"))
                        .city(addressComponent.getStr("city"))
                        .district(addressComponent.getStr("district"))
                        .address(result.getStr("address"))
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();
            }
        } catch (Exception e) {
            log.error("逆地理编码异常: lat={}, lng={}, error={}", latitude, longitude, e.getMessage());
        }
        return AddressVO.builder()
                .latitude(latitude).longitude(longitude)
                .address("解析失败")
                .build();
    }

    @Override
    public CityVO getCurrentCity(Double latitude, Double longitude) {
        AddressVO address = reverseGeocode(latitude, longitude);
        if (address == null || StrUtil.isBlank(address.getCity())) {
            return CityVO.builder().name("未知").build();
        }
        return CityVO.builder()
                .name(address.getCity())
                .province(address.getProvince())
                .build();
    }

    @Override
    public List<CityVO> getHotCities() {
        String hotCitiesJson = getConfigValue("city.hot.list",
                "[\"北京\",\"上海\",\"广州\",\"深圳\",\"杭州\",\"成都\"]");
        try {
            JSONArray array = JSONUtil.parseArray(hotCitiesJson);
            List<CityVO> cities = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                String name = array.getStr(i);
                cities.add(CityVO.builder().name(name).build());
            }
            return cities;
        } catch (Exception e) {
            log.error("解析热门城市配置异常", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, List<CityVO>> getAllCities() {
        // 返回常用城市列表，按首字母分组
        Map<String, List<CityVO>> grouped = new LinkedHashMap<>();

        // 城市数据（生产环境可从配置或数据库读取）
        String[][] cities = {
                {"A", "阿坝"},
                {"B", "北京"}, {"B", "保定"}, {"B", "包头"},
                {"C", "成都"}, {"C", "重庆"}, {"C", "长沙"}, {"C", "长春"}, {"C", "常州"},
                {"D", "大连"}, {"D", "东莞"}, {"D", "大理"},
                {"F", "福州"}, {"F", "佛山"},
                {"G", "广州"}, {"G", "贵阳"}, {"G", "桂林"},
                {"H", "杭州"}, {"H", "合肥"}, {"H", "海口"}, {"H", "哈尔滨"}, {"H", "惠州"},
                {"J", "济南"}, {"J", "嘉兴"},
                {"K", "昆明"},
                {"L", "兰州"}, {"L", "洛阳"},
                {"N", "南京"}, {"N", "宁波"}, {"N", "南宁"}, {"N", "南昌"},
                {"Q", "青岛"},
                {"S", "上海"}, {"S", "深圳"}, {"S", "苏州"}, {"S", "沈阳"}, {"S", "石家庄"},
                {"T", "天津"}, {"T", "太原"},
                {"W", "武汉"}, {"W", "无锡"}, {"W", "温州"},
                {"X", "西安"}, {"X", "厦门"}, {"X", "徐州"},
                {"Y", "银川"},
                {"Z", "郑州"}, {"Z", "珠海"},
        };

        for (String[] city : cities) {
            String initial = city[0];
            String name = city[1];
            grouped.computeIfAbsent(initial, k -> new ArrayList<>())
                    .add(CityVO.builder().code(name).name(name).initial(initial).build());
        }
        return grouped;
    }

    @Override
    public List<CityVO> searchCities(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return Collections.emptyList();
        }
        Map<String, List<CityVO>> allCities = getAllCities();
        List<CityVO> result = new ArrayList<>();
        for (List<CityVO> cityList : allCities.values()) {
            for (CityVO city : cityList) {
                if (city.getName().contains(keyword) || city.getInitial().contains(keyword.toUpperCase())) {
                    result.add(city);
                }
            }
        }
        return result;
    }

    /**
     * 从 sys_config 读取配置值
     */
    private String getConfigValue(String configKey, String defaultValue) {
        try {
            SysConfig config = sysConfigService.getByConfigKey(configKey);
            return config != null && StrUtil.isNotBlank(config.getConfigValue())
                    ? config.getConfigValue() : defaultValue;
        } catch (Exception e) {
            log.debug("读取配置 {} 异常，使用默认值", configKey);
            return defaultValue;
        }
    }
}
