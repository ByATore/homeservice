package com.homeservice.order.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentVO {

    private Long orderId;

    private String orderNo;

    private String paymentNo;

    /** 支付方式：1=微信 2=支付宝 */
    private Integer paymentMethod;

    private BigDecimal paymentAmount;

    /** 第三方交易号 */
    private String transactionId;

    /** 支付状态：1=待支付 2=已支付 3=支付失败 4=已退款 */
    private Integer status;

    private LocalDateTime paidAt;

    private BigDecimal refundAmount;

    private LocalDateTime refundAt;

    private String refundReason;
}
