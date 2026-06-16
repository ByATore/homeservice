package com.homeservice.media.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homeservice.media.entity.Banner;

import java.util.List;
import java.util.Map;

/**
 * Banner管理服务接口
 */
public interface BannerService extends IService<Banner> {

    /**
     * 获取启用中的Banner列表（按位置）
     */
    List<Banner> getActiveBanners(String position);

    /**
     * 分页查询Banner（管理后台）
     */
    Page<Banner> getBannerPage(Integer current, Integer size, String position, Integer status);

    /**
     * 更新Banner状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 批量更新排序
     */
    boolean updateSort(List<Map<String, Object>> sortList);
}
