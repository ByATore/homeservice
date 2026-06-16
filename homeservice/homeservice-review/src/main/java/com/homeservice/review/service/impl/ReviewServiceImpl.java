package com.homeservice.review.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homeservice.review.entity.Review;
import com.homeservice.review.entity.vo.ReviewVO;
import com.homeservice.review.mapper.ReviewMapper;
import com.homeservice.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Override
    @Transactional
    public boolean replyReview(Long reviewId, String reply) {
        Review review = getById(reviewId);
        if (review == null) {
            return false;
        }
        review.setReply(reply);
        review.setReplyTime(LocalDateTime.now());
        return updateById(review);
    }

    @Override
    @Cacheable(value = "review:worker", key = "#workerId + '_' + #limit")
    public List<ReviewVO> getReviewsByWorkerId(Long workerId, Integer limit) {
        return baseMapper.selectReviewsByWorkerId(workerId, limit);
    }

    @Override
    public IPage<ReviewVO> getReviewPageWithDetails(Page<ReviewVO> page, Long workerId,
                                                     Long userId, Long orderId, Integer status) {
        return baseMapper.selectReviewPageWithDetails(page, workerId, userId, orderId, status);
    }
}