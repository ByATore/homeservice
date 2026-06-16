package com.homeservice.common.config;

import com.homeservice.common.constant.RabbitMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(ConnectionFactory.class)
public class RabbitMqConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitMqConstants.TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderPaidQueue() {
        return QueueBuilder.durable(RabbitMqConstants.ORDER_PAID_QUEUE).build();
    }

    @Bean
    public Queue notifySmsQueue() {
        return QueueBuilder.durable(RabbitMqConstants.NOTIFY_SMS_QUEUE).build();
    }

    @Bean
    public Queue notifyOrderQueue() {
        return QueueBuilder.durable(RabbitMqConstants.NOTIFY_ORDER_QUEUE).build();
    }

    @Bean
    public Queue notifyDispatchQueue() {
        return QueueBuilder.durable(RabbitMqConstants.NOTIFY_DISPATCH_QUEUE).build();
    }

    @Bean
    public Binding orderPaidBinding() {
        return BindingBuilder.bind(orderPaidQueue()).to(topicExchange()).with(RabbitMqConstants.ORDER_PAID_KEY);
    }

    @Bean
    public Binding notifySmsBinding() {
        return BindingBuilder.bind(notifySmsQueue()).to(topicExchange()).with(RabbitMqConstants.NOTIFY_SMS_KEY);
    }

    @Bean
    public Binding notifyOrderBinding() {
        return BindingBuilder.bind(notifyOrderQueue()).to(topicExchange()).with(RabbitMqConstants.NOTIFY_ORDER_KEY);
    }

    @Bean
    public Binding notifyDispatchBinding() {
        return BindingBuilder.bind(notifyDispatchQueue()).to(topicExchange()).with(RabbitMqConstants.NOTIFY_DISPATCH_KEY);
    }
}