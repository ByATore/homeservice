package com.homeservice.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homeservice.user.entity.Worker;
import com.homeservice.user.entity.vo.WorkerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkerMapper extends BaseMapper<Worker> {

    @Select("SELECT w.*, w.user_real_name, w.user_phone " +
            "FROM worker w " +
            "WHERE w.status = 1 " +
            "ORDER BY w.rating DESC, w.service_count DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectTopWorkers(@Param("limit") Integer limit);

    @Select("<script>" +
            "SELECT w.*, " +
            "u.real_name AS user_real_name, u.phone AS user_phone, " +
            "u.avatar AS user_avatar, u.gender AS user_gender " +
            "FROM worker w " +
            "LEFT JOIN `user` u ON w.user_id = u.id " +
            "<where>" +
            "<if test='workerNo != null and workerNo != \"\"'>AND w.worker_no LIKE CONCAT('%', #{workerNo}, '%')</if>" +
            "<if test='status != null'>AND w.status = #{status}</if>" +
            "<if test='specialty != null and specialty != \"\"'>AND w.specialty LIKE CONCAT('%', #{specialty}, '%')</if>" +
            "</where>" +
            "ORDER BY w.created_at DESC" +
            "</script>")
    IPage<WorkerVO> selectWorkerPageWithUser(Page<WorkerVO> page,
                                              @Param("workerNo") String workerNo,
                                              @Param("status") Integer status,
                                              @Param("specialty") String specialty);

    @Select("SELECT w.*, " +
            "u.real_name AS user_real_name, u.phone AS user_phone, " +
            "u.avatar AS user_avatar, u.gender AS user_gender " +
            "FROM worker w " +
            "LEFT JOIN `user` u ON w.user_id = u.id " +
            "WHERE w.id = #{id}")
    WorkerVO selectWorkerDetailById(@Param("id") Long id);

    @Select("SELECT w.*, " +
            "u.real_name AS user_real_name, u.phone AS user_phone, " +
            "u.avatar AS user_avatar, u.gender AS user_gender " +
            "FROM worker w " +
            "LEFT JOIN `user` u ON w.user_id = u.id " +
            "WHERE w.status = 1 " +
            "ORDER BY w.rating DESC, w.service_count DESC " +
            "LIMIT #{limit}")
    List<WorkerVO> selectAvailableWorkersByLimit(@Param("limit") Integer limit);
}