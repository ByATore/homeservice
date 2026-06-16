package com.homeservice.order.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信支付服务接口
 */
public interface WechatPayService {

    /**
     * 创建微信支付订单
     *
     * @param orderId  订单ID
     * @param orderNo  订单号
     * @param amount   支付金额（元）
     * @param openid   用户微信openid
     * @return 返回支付参数（包含 prepay_id、nonceStr、timeStamp、sign 等）
     */
    Map<String, Object> createPayment(Long orderId, String orderNo, BigDecimal amount, String openid);

    /**
     * 验证微信支付回调签名
     *
     * @param body       回调请求体
     * @param signature  微信签名
     * @return 验证通过返回true
     */
    boolean verifyCallback(String body, String signature);

    /**
     * 查询微信支付订单
     *
     * @param transactionId 微信交易号
     * @return 订单信息
     */
    Map<String, Object> queryOrder(String transactionId);

    /**
     * 发起微信退款
     *
     * @param transactionId  微信交易号
     * @param outRefundNo    商户退款单号
     * @param totalAmount    订单总金额（元）
     * @param refundAmount   退款金额（元）
     * @return 退款结果
     */
    boolean refund(String transactionId, String outRefundNo, BigDecimal totalAmount, BigDecimal refundAmount);
}
