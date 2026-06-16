package com.homeservice.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homeservice.service.entity.ServiceItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceItemMapper extends BaseMapper<ServiceItem> {
}