package com.homeservice.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.user.entity.Worker;
import com.homeservice.user.entity.vo.WorkerVO;
import com.homeservice.user.mapper.WorkerMapper;
import com.homeservice.user.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerServiceImpl extends ServiceImpl<WorkerMapper, Worker> implements WorkerService {

    @Override
    public Worker getWorkerByNo(String workerNo) {
        LambdaQueryWrapper<Worker> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Worker::getWorkerNo, workerNo);
        return getOne(wrapper);
    }

    @Override
    public WorkerVO getWorkerDetailById(Long id) {
        return baseMapper.selectWorkerDetailById(id);
    }

    @Override
    public IPage<WorkerVO> getWorkerPageWithUser(Page<WorkerVO> page, String workerNo,
                                                  Integer status, String specialty) {
        return baseMapper.selectWorkerPageWithUser(page, workerNo, status, specialty);
    }

    @Override
    @Cacheable(value = "worker:available", key = "#limit")
    public List<WorkerVO> getAvailableWorkers(Integer limit) {
        return baseMapper.selectAvailableWorkersByLimit(limit);
    }

    @Override
    @Transactional
    public boolean updateWorkerRating(Long workerId, java.math.BigDecimal rating) {
        Worker worker = getById(workerId);
        if (worker == null) {
            return false;
        }
        worker.setRating(rating);
        return updateById(worker);
    }

    @Override
    @Transactional
    public boolean incrementServiceCount(Long workerId) {
        Worker worker = getById(workerId);
        if (worker == null) {
            return false;
        }
        worker.setServiceCount(worker.getServiceCount() + 1);
        return updateById(worker);
    }

    @Override
    @Transactional
    public boolean updateWorkerEarnings(Long workerId, java.math.BigDecimal amount) {
        Worker worker = getById(workerId);
        if (worker == null) {
            return false;
        }
        worker.setTotalEarnings(worker.getTotalEarnings().add(amount));
        return updateById(worker);
    }
}