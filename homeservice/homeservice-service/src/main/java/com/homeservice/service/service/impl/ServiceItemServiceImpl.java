package com.homeservice.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.service.entity.ServiceItem;
import com.homeservice.service.mapper.ServiceItemMapper;
import com.homeservice.service.service.ServiceItemService;
import org.springframework.stereotype.Service;

@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements ServiceItemService {
}