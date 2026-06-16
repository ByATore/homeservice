package com.homeservice.order.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.homeservice.order.service.WechatPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付服务实现
 * <p>
 * 基于微信支付 V3 API，使用 Hutool 发送 HTTP 请求。
 * 生产环境需要从 sys_config 或环境变量读取商户配置。
 */
@Slf4j
@Service
public class WechatPayServiceImpl implements WechatPayService {

    /** 微信支付 API 基础地址 */
    private static final String WECHAT_PAY_BASE_URL = "https://api.mch.weixin.qq.com";

    /** JSAPI 下单 */
    private static final String JSAPI_ORDER_URL = WECHAT_PAY_BASE_URL + "/v3/pay/transactions/jsapi";

    /** 查询订单 */
    private static final String QUERY_ORDER_URL = WECHAT_PAY_BASE_URL + "/v3/pay/transactions/id/{transaction_id}";

    /** 申请退款 */
    private static final String REFUND_URL = WECHAT_PAY_BASE_URL + "/v3/refund/domestic/refunds";

    @Override
    public Map<String, Object> createPayment(Long orderId, String orderNo, BigDecimal amount, String openid) {
        // 将元转换为分
        int amountInFen = amount.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue();

        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("appid", getAppId());
        reqBody.put("mchid", getMchId());
        reqBody.put("description", "家政服务-" + orderNo);
        reqBody.put("out_trade_no", orderNo);
        reqBody.put("notify_url", getNotifyUrl());

        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("total", amountInFen);
        amountMap.put("currency", "CNY");
        reqBody.put("amount", amountMap);

        Map<String, Object> payerMap = new HashMap<>();
        payerMap.put("openid", openid);
        reqBody.put("payer", payerMap);

        try {
            String response = HttpUtil.createPost(JSAPI_ORDER_URL)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(JSONUtil.toJsonStr(reqBody))
                    .execute()
                    .body();

            JSONObject respJson = JSONUtil.parseObj(response);
            Map<String, Object> result = new HashMap<>();
            result.put("prepay_id", respJson.getStr("prepay_id"));
            result.put("orderNo", orderNo);
            result.put("amount", amount);
            log.info("微信支付下单成功: orderNo={}, prepay_id={}", orderNo, respJson.getStr("prepay_id"));
            return result;
        } catch (Exception e) {
            log.error("微信支付下单失败: orderNo={}, error={}", orderNo, e.getMessage(), e);
            throw new RuntimeException("微信支付下单失败: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyCallback(String body, String signature) {
        if (StrUtil.isBlank(signature)) {
            log.warn("微信支付回调签名为空");
            return false;
        }
        // 生产环境需使用微信 SDK 验证签名
        // 此处为简化实现，生产环境请使用 WechatPay2Validator
        log.info("微信支付回调验证: body length={}, signature={}", body.length(), signature);
        return true;
    }

    @Override
    public Map<String, Object> queryOrder(String transactionId) {
        try {
            String url = StrUtil.replace(QUERY_ORDER_URL, "{transaction_id}", transactionId);
            String response = HttpUtil.createGet(url)
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            JSONObject respJson = JSONUtil.parseObj(response);
            Map<String, Object> result = new HashMap<>();
            result.put("transactionId", transactionId);
            result.put("tradeState", respJson.getStr("trade_state"));
            result.put("amount", respJson.getByPath("amount.total"));
            log.info("微信支付查询成功: transactionId={}, state={}", transactionId, respJson.getStr("trade_state"));
            return result;
        } catch (Exception e) {
            log.error("微信支付查询失败: transactionId={}, error={}", transactionId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean refund(String transactionId, String outRefundNo, BigDecimal totalAmount, BigDecimal refundAmount) {
        int refundFen = refundAmount.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue();
        int totalFen = totalAmount.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue();

        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("transaction_id", transactionId);
        reqBody.put("out_refund_no", outRefundNo);

        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("refund", refundFen);
        amountMap.put("total", totalFen);
        amountMap.put("currency", "CNY");
        reqBody.put("amount", amountMap);

        try {
            String response = HttpUtil.createPost(REFUND_URL)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(JSONUtil.toJsonStr(reqBody))
                    .execute()
                    .body();

            JSONObject respJson = JSONUtil.parseObj(response);
            String status = respJson.getStr("status");
            log.info("微信退款: transactionId={}, outRefundNo={}, status={}", transactionId, outRefundNo, status);
            return "SUCCESS".equals(status) || "PROCESSING".equals(status);
        } catch (Exception e) {
            log.error("微信退款失败: transactionId={}, error={}", transactionId, e.getMessage(), e);
            return false;
        }
    }

    // ========== 配置获取（生产环境应从 sys_config 或环境变量读取） ==========

    private String getAppId() {
        return System.getProperty("wechat.pay.appid", "");
    }

    private String getMchId() {
        return System.getProperty("wechat.pay.mchid", "");
    }

    private String getNotifyUrl() {
        return System.getProperty("wechat.pay.notify_url", "");
    }
}
