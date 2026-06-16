package com.homeservice.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homeservice.user.entity.Worker;
import com.homeservice.user.entity.vo.WorkerVO;

import java.util.List;

public interface WorkerService extends IService<Worker> {
    Worker getWorkerByNo(String workerNo);

    WorkerVO getWorkerDetailById(Long id);

    IPage<WorkerVO> getWorkerPageWithUser(Page<WorkerVO> page, String workerNo,
                                           Integer status, String specialty);

    List<WorkerVO> getAvailableWorkers(Integer limit);

    boolean updateWorkerRating(Long workerId, java.math.BigDecimal rating);

    boolean incrementServiceCount(Long workerId);

    boolean updateWorkerEarnings(Long workerId, java.math.BigDecimal amount);
}