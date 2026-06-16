package com.homeservice.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mq_message")
public class MqMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String messageId;

    private String exchange;

    private String routingKey;

    private String messageBody;

    private Integer status;

    private Integer retryCount;

    private Integer maxRetry;

    private LocalDateTime nextRetryTime;

    private String errorMsg;

    private String businessType;

    private String businessId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}