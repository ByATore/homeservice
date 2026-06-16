package com.homeservice.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.review.entity.Review;
import com.homeservice.review.entity.vo.ReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    @Select("SELECT r.*, " +
            "r.user_real_name, r.worker_real_name, r.service_name " +
            "FROM review r " +
            "WHERE r.worker_id = #{workerId} " +
            "ORDER BY r.created_at DESC " +
            "LIMIT #{limit}")
    List<ReviewVO> selectReviewsByWorkerId(@Param("workerId") Long workerId, @Param("limit") Integer limit);

    @Select("<script>" +
            "SELECT r.*, " +
            "r.user_real_name, r.worker_real_name, r.service_name " +
            "FROM review r " +
            "<where>" +
            "<if test='workerId != null'>AND r.worker_id = #{workerId}</if>" +
            "<if test='userId != null'>AND r.user_id = #{userId}</if>" +
            "<if test='orderId != null'>AND r.order_id = #{orderId}</if>" +
            "<if test='status != null'>AND r.status = #{status}</if>" +
            "</where>" +
            "ORDER BY r.created_at DESC" +
            "</script>")
    IPage<ReviewVO> selectReviewPageWithDetails(Page<ReviewVO> page,
                                                  @Param("workerId") Long workerId,
                                                  @Param("userId") Long userId,
                                                  @Param("orderId") Long orderId,
                                                  @Param("status") Integer status);
}