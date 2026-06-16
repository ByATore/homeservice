package com.homeservice.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homeservice.statistics.entity.Statistics;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StatisticsMapper extends BaseMapper<Statistics> {
}