package com.homeservice.common.constant;

public class RabbitMqConstants {

    private RabbitMqConstants() {}

    public static final String TOPIC_EXCHANGE = "homeservice.topic.exchange";

    public static final String ORDER_PAID_QUEUE = "order.paid.queue";
    public static final String NOTIFY_SMS_QUEUE = "notify.sms.queue";
    public static final String NOTIFY_ORDER_QUEUE = "notify.order.queue";
    public static final String NOTIFY_DISPATCH_QUEUE = "notify.dispatch.queue";

    public static final String ORDER_PAID_KEY = "order.paid";
    public static final String NOTIFY_SMS_KEY = "notify.sms";
    public static final String NOTIFY_ORDER_KEY = "notify.order";
    public static final String NOTIFY_DISPATCH_KEY = "notify.dispatch";
}