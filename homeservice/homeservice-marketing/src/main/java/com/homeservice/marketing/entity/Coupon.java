package com.homeservice.marketing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String description;
    
    private Integer type;
    
    private BigDecimal discountAmount;
    
    private BigDecimal discountRate;
    
    private BigDecimal minAmount;
    
    private BigDecimal maxDiscount;
    
    private Integer totalQuantity;
    
    private Integer usedQuantity;
    
    private Integer validDays;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}