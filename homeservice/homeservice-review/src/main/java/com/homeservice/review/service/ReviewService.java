package com.homeservice.review.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homeservice.review.entity.Review;
import com.homeservice.review.entity.vo.ReviewVO;

import java.util.List;

public interface ReviewService extends IService<Review> {
    boolean replyReview(Long reviewId, String reply);

    List<ReviewVO> getReviewsByWorkerId(Long workerId, Integer limit);

    IPage<ReviewVO> getReviewPageWithDetails(Page<ReviewVO> page, Long workerId,
                                              Long userId, Long orderId, Integer status);
}