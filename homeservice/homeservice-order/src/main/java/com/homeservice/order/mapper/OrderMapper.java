package com.homeservice.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.order.entity.Order;
import com.homeservice.order.entity.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT `status`, COUNT(*) as count FROM `order` GROUP BY `status`")
    List<Map<String, Object>> selectStatusDistribution();

    @Select("SELECT DATE_FORMAT(created_at, #{dateFormat}) as period_label, " +
            "`status`, SUM(actual_amount) as total_amount, COUNT(*) as order_count " +
            "FROM `order` " +
            "WHERE created_at >= #{startTime} AND created_at < #{endTime} " +
            "GROUP BY period_label, `status` " +
            "ORDER BY period_label ASC")
    List<Map<String, Object>> selectOrderTrend(@Param("dateFormat") String dateFormat,
                                                @Param("startTime") String startTime,
                                                @Param("endTime") String endTime);

    @Select("SELECT COUNT(*) as total_orders, COALESCE(SUM(actual_amount), 0) as total_revenue FROM `order`")
    Map<String, Object> selectTotalStats();

    @Select("SELECT service_name as name, COUNT(*) as count FROM `order` GROUP BY service_name ORDER BY count DESC LIMIT #{limit}")
    List<Map<String, Object>> selectTopServices(@Param("limit") Integer limit);

    @Select("<script>" +
            "SELECT o.*, " +
            "u.real_name AS user_real_name, u.phone AS user_phone, " +
            "w.user_real_name AS worker_real_name, w.user_phone AS worker_phone, " +
            "w.rating AS worker_rating, w.service_count AS worker_service_count, " +
            "si.name AS service_item_name, si.unit AS service_item_unit, " +
            "sc.name AS category_name " +
            "FROM `order` o " +
            "LEFT JOIN `user` u ON o.user_id = u.id " +
            "LEFT JOIN `worker` w ON o.worker_id = w.id " +
            "LEFT JOIN `service_item` si ON o.service_item_id = si.id " +
            "LEFT JOIN `service_category` sc ON si.category_id = sc.id " +
            "<where>" +
            "<if test='orderNo != null and orderNo != \"\"'>AND o.order_no LIKE CONCAT('%', #{orderNo}, '%')</if>" +
            "<if test='userId != null'>AND o.user_id = #{userId}</if>" +
            "<if test='workerId != null'>AND o.worker_id = #{workerId}</if>" +
            "<if test='status != null'>AND o.status = #{status}</if>" +
            "</where>" +
            "ORDER BY o.created_at DESC" +
            "</script>")
    IPage<OrderVO> selectOrderPageWithDetails(Page<OrderVO> page,
                                               @Param("orderNo") String orderNo,
                                               @Param("userId") Long userId,
                                               @Param("workerId") Long workerId,
                                               @Param("status") Integer status);

    @Select("SELECT o.*, " +
            "u.real_name AS user_real_name, u.phone AS user_phone, " +
            "w.user_real_name AS worker_real_name, w.user_phone AS worker_phone, " +
            "w.rating AS worker_rating, w.service_count AS worker_service_count, " +
            "si.name AS service_item_name, si.unit AS service_item_unit, " +
            "sc.name AS category_name " +
            "FROM `order` o " +
            "LEFT JOIN `user` u ON o.user_id = u.id " +
            "LEFT JOIN `worker` w ON o.worker_id = w.id " +
            "LEFT JOIN `service_item` si ON o.service_item_id = si.id " +
            "LEFT JOIN `service_category` sc ON si.category_id = sc.id " +
            "WHERE o.id = #{id}")
    OrderVO selectOrderDetailById(@Param("id") Long id);

    @Select("SELECT o.*, " +
            "u.real_name AS user_real_name, u.phone AS user_phone, " +
            "w.user_real_name AS worker_real_name, w.user_phone AS worker_phone, " +
            "w.rating AS worker_rating, w.service_count AS worker_service_count, " +
            "si.name AS service_item_name, si.unit AS service_item_unit, " +
            "sc.name AS category_name " +
            "FROM `order` o " +
            "LEFT JOIN `user` u ON o.user_id = u.id " +
            "LEFT JOIN `worker` w ON o.worker_id = w.id " +
            "LEFT JOIN `service_item` si ON o.service_item_id = si.id " +
            "LEFT JOIN `service_category` sc ON si.category_id = sc.id " +
            "WHERE o.order_no = #{orderNo}")
    OrderVO selectOrderDetailByNo(@Param("orderNo") String orderNo);

    @Select("SELECT o.*, " +
            "u.real_name AS user_real_name, u.phone AS user_phone, " +
            "w.user_real_name AS worker_real_name, " +
            "si.name AS service_item_name " +
            "FROM `order` o " +
            "LEFT JOIN `user` u ON o.user_id = u.id " +
            "LEFT JOIN `worker` w ON o.worker_id = w.id " +
            "LEFT JOIN `service_item` si ON o.service_item_id = si.id " +
            "WHERE o.status IN (2, 3) " +
            "ORDER BY o.service_time ASC " +
            "LIMIT #{limit}")
    List<OrderVO> selectPendingOrders(@Param("limit") Integer limit);
}