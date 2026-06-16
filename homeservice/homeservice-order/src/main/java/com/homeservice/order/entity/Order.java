package com.homeservice.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long workerId;
    
    private Long serviceItemId;
    
    private String serviceName;
    
    private BigDecimal servicePrice;
    
    private LocalDateTime serviceTime;
    
    private String serviceAddress;
    
    private Integer serviceDuration;
    
    private String contactName;
    
    private String contactPhone;
    
    private String remark;
    
    private BigDecimal totalAmount;
    
    private BigDecimal discountAmount;
    
    private BigDecimal actualAmount;
    
    private Long couponId;
    
    private Integer status;
    
    private String cancelReason;

    private LocalDateTime cancelTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    /** 支付流水号 */
    private String paymentNo;

    /** 支付方式：1=微信 2=支付宝 */
    private Integer paymentMethod;

    /** 第三方交易号（微信/支付宝返回） */
    private String transactionId;

    /** 支付时间 */
    private LocalDateTime paidAt;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 退款时间 */
    private LocalDateTime refundAt;

    /** 退款原因 */
    private String refundReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}