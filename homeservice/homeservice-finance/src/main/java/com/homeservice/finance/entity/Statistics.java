package com.homeservice.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("statistics")
public class Statistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private LocalDate statDate;
    
    private Integer totalUsers;
    private Integer newUsers;
    private Integer totalWorkers;
    private Integer newWorkers;
    private Integer totalOrders;
    private Integer newOrders;
    private Integer completedOrders;
    private BigDecimal totalAmount;
    private BigDecimal totalIncome;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}