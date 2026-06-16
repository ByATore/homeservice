package com.homeservice.media.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homeservice.media.entity.DecorationComponent;

import java.util.List;
import java.util.Map;

/**
 * 装修组件服务接口
 */
public interface DecorationService extends IService<DecorationComponent> {

    /**
     * 获取指定页面的装修数据
     */
    Map<String, Object> getPageDecoration(String page);

    /**
     * 分页查询装修组件（管理后台）
     */
    Page<DecorationComponent> getComponentPage(Integer current, Integer size, String page, String componentType);

    /**
     * 批量更新排序
     */
    boolean updateSort(List<Map<String, Object>> sortList);

    /**
     * 复制组件
     */
    boolean copyComponent(Long id);
}
