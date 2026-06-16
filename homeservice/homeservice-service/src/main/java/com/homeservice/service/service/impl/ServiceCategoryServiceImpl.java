package com.homeservice.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.service.entity.ServiceCategory;
import com.homeservice.service.mapper.ServiceCategoryMapper;
import com.homeservice.service.service.ServiceCategoryService;
import org.springframework.stereotype.Service;

@Service
public class ServiceCategoryServiceImpl extends ServiceImpl<ServiceCategoryMapper, ServiceCategory> implements ServiceCategoryService {
}