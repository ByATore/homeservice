package com.homeservice.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homeservice.common.entity.MqMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MqMessageMapper extends BaseMapper<MqMessage> {

    @Select("SELECT * FROM mq_message WHERE status = 0 AND next_retry_time <= #{now} ORDER BY next_retry_time ASC LIMIT #{limit}")
    List<MqMessage> selectPendingMessages(@Param("now") LocalDateTime now, @Param("limit") int limit);
}