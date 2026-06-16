package com.homeservice.review.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.common.result.Result;
import com.homeservice.review.entity.Review;
import com.homeservice.review.entity.vo.ReviewVO;
import com.homeservice.review.service.ReviewService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "评价管理")
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "分页查询评价列表（含快照信息）")
    @GetMapping("/page")
    public Result<IPage<ReviewVO>> getReviewPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer status) {

        Page<ReviewVO> page = new Page<>(current, size);
        IPage<ReviewVO> result = reviewService.getReviewPageWithDetails(
                page, workerId, null, null, status);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询评价")
    @GetMapping("/{id}")
    public Result<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getById(id);
        if (review == null) {
            return Result.error("评价不存在");
        }
        return Result.success(review);
    }

    @Operation(summary = "根据服务人员ID查询评价（含快照信息）")
    @GetMapping("/worker/{workerId}")
    public Result<List<ReviewVO>> getReviewsByWorkerId(
            @PathVariable Long workerId,
            @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(reviewService.getReviewsByWorkerId(workerId, limit));
    }

    @Operation(summary = "创建评价")
    @PostMapping
    public Result<Review> createReview(@RequestBody Review review) {
        review.setStatus(1);
        boolean saved = reviewService.save(review);
        return saved ? Result.success(review) : Result.error("创建失败");
    }

    @Operation(summary = "回复评价")
    @PutMapping("/{id}/reply")
    public Result<Void> replyReview(@PathVariable Long id, @RequestParam String reply) {
        boolean replied = reviewService.replyReview(id, reply);
        return replied ? Result.success() : Result.error("回复失败");
    }

    @Operation(summary = "更新评价状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateReviewStatus(@PathVariable Long id, @RequestParam Integer status) {
        Review review = new Review();
        review.setId(id);
        review.setStatus(status);
        boolean updated = reviewService.updateById(review);
        return updated ? Result.success() : Result.error("更新失败");
    }

    @Operation(summary = "删除评价")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        boolean deleted = reviewService.removeById(id);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "批量删除评价")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteReviews(@RequestBody List<Long> ids) {
        boolean deleted = reviewService.removeByIds(ids);
        return deleted ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "获取评价评分汇总")
    @GetMapping("/summary")
    public Result<Map<String, Object>> getReviewSummary(@RequestParam Long serviceItemId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getServiceItemId, serviceItemId);
        wrapper.eq(Review::getStatus, 1);
        List<Review> reviews = reviewService.list(wrapper);

        Map<String, Object> summary = new HashMap<>();
        if (reviews.isEmpty()) {
            summary.put("averageRating", 0.0);
            summary.put("totalCount", 0);
            summary.put("ratingDistribution", new HashMap<>());
            return Result.success(summary);
        }

        double avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        avgRating = Math.round(avgRating * 10.0) / 10.0;

        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 5; i >= 1; i--) {
            distribution.put(i, 0L);
        }
        for (Review review : reviews) {
            int rating = review.getRating();
            distribution.merge(rating, 1L, Long::sum);
        }

        summary.put("averageRating", avgRating);
        summary.put("totalCount", reviews.size());
        summary.put("ratingDistribution", distribution);
        return Result.success(summary);
    }
}