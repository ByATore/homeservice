package com.homeservice.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("worker")
public class Worker {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;

    private String userRealName;

    private String userPhone;
    
    private String workerNo;
    
    private String idCard;
    
    private String education;
    
    private Integer experienceYears;
    
    private String specialty;
    
    private String selfIntroduction;
    
    private BigDecimal rating;
    
    private Integer serviceCount;
    
    private BigDecimal totalEarnings;
    
    private String certificates;
    
    private String workAreas;
    
    private String availableTime;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}