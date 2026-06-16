package com.homeservice.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private String orderNo;
    
    private Long userId;

    private String userRealName;
    
    private Long workerId;

    private String workerRealName;
    
    private Long serviceItemId;

    private String serviceName;
    
    private Integer rating;
    
    private String content;
    
    private String images;
    
    private String tags;
    
    private String reply;
    
    private LocalDateTime replyTime;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}