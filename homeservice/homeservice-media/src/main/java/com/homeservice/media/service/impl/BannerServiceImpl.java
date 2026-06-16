package com.homeservice.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.media.entity.Banner;
import com.homeservice.media.mapper.BannerMapper;
import com.homeservice.media.service.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<Banner> getActiveBanners(String position) {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1);
        wrapper.eq(Banner::getPosition, position != null ? position : "home");
        wrapper.le(Banner::getStartTime, now);
        wrapper.and(w -> w.isNull(Banner::getEndTime).or().ge(Banner::getEndTime, now));
        wrapper.orderByAsc(Banner::getSort);
        return list(wrapper);
    }

    @Override
    public Page<Banner> getBannerPage(Integer current, Integer size, String position, Integer status) {
        Page<Banner> page = new Page<>(current, size);
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (position != null) {
            wrapper.eq(Banner::getPosition, position);
        }
        if (status != null) {
            wrapper.eq(Banner::getStatus, status);
        }
        wrapper.orderByAsc(Banner::getSort);
        return page(page, wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setStatus(status);
        return updateById(banner);
    }

    @Override
    public boolean updateSort(List<Map<String, Object>> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            return false;
        }
        for (Map<String, Object> item : sortList) {
            Long id = Long.valueOf(item.get("id").toString());
            Integer sort = Integer.valueOf(item.get("sort").toString());
            Banner banner = new Banner();
            banner.setId(id);
            banner.setSort(sort);
            updateById(banner);
        }
        return true;
    }
}
