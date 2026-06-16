package com.homeservice.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homeservice.service.entity.ServiceCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceCategoryMapper extends BaseMapper<ServiceCategory> {
}