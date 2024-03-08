package com.frozendo.pennysave.consumer.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_CREATE_KEY;
import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_DIRECT_EXCHANGE;
import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_DLQ_DIRECT_EXCHANGE;
import static com.frozendo.pennysave.domain.enums.PersonQueueEnum.EMAIL_NOTIFICATION_PERSON_EVENT;

@Configuration
public class EmailConfirmationPersonEventConfig {

    private final RabbitAdmin rabbitAdmin;

    private final ConnectionFactory connectionFactory;

    private final Jackson2JsonMessageConverter jackson2JsonMessageConverter;

    public EmailConfirmationPersonEventConfig(RabbitAdmin rabbitAdmin,
                                              @Qualifier("rabbitConnectionFactory") ConnectionFactory connectionFactory,
                                              Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        this.rabbitAdmin = rabbitAdmin;
        this.connectionFactory = connectionFactory;
        this.jackson2JsonMessageConverter = jackson2JsonMessageConverter;
    }

    @PostConstruct
    public void emailConfirmationPersonComponents() {
        createEmailConfirmationQueues();
        bindingEmailConfirmationQueues();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory emailConfirmationContainerFactory() {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        return factory;
    }

    private void createEmailConfirmationQueues() {
        createMainQueue();
        createDelayedQueue();
        createDlqQueue();
    }

    private void bindingEmailConfirmationQueues() {
        bindingMainQueueToPersonCreateEvent();
        bindingMainQueueToRetries();
        bindingDelayedQueue();
        bindingDlqQueue();
    }

    private void createMainQueue() {
        var queue = QueueBuilder
                .durable(EMAIL_NOTIFICATION_PERSON_EVENT.getQueue())
                .deadLetterRoutingKey(EMAIL_NOTIFICATION_PERSON_EVENT.getDlqQueue())
                .deadLetterExchange(PERSON_DLQ_DIRECT_EXCHANGE.getProperty())
                .build();
        rabbitAdmin.declareQueue(queue);
    }

    private void createDelayedQueue() {
        var delayedQueue = QueueBuilder
                .durable(EMAIL_NOTIFICATION_PERSON_EVENT.getDelayedQueue())
                .deadLetterRoutingKey(EMAIL_NOTIFICATION_PERSON_EVENT.getQueue())
                .deadLetterExchange(PERSON_DIRECT_EXCHANGE.getProperty())
                .build();
        rabbitAdmin.declareQueue(delayedQueue);
    }

    private void createDlqQueue() {
        var dlqQueue = QueueBuilder
                .durable(EMAIL_NOTIFICATION_PERSON_EVENT.getDlqQueue())
                .build();
        rabbitAdmin.declareQueue(dlqQueue);
    }

    private void bindingMainQueueToPersonCreateEvent() {
        var bindingQueue = new Binding(EMAIL_NOTIFICATION_PERSON_EVENT.getQueue(),
                Binding.DestinationType.QUEUE,
                PERSON_DIRECT_EXCHANGE.getProperty(),
                PERSON_CREATE_KEY.getProperty(),
                null);
        rabbitAdmin.declareBinding(bindingQueue);
    }

    private void bindingMainQueueToRetries() {
        var bindingQueueRetry = new Binding(EMAIL_NOTIFICATION_PERSON_EVENT.getQueue(),
                Binding.DestinationType.QUEUE,
                PERSON_DIRECT_EXCHANGE.getProperty(),
                EMAIL_NOTIFICATION_PERSON_EVENT.getQueue(),
                null);
        rabbitAdmin.declareBinding(bindingQueueRetry);
    }

    private void bindingDelayedQueue() {
        var bindingDelayedQueue = new Binding(EMAIL_NOTIFICATION_PERSON_EVENT.getDelayedQueue(),
                Binding.DestinationType.QUEUE,
                PERSON_DLQ_DIRECT_EXCHANGE.getProperty(),
                EMAIL_NOTIFICATION_PERSON_EVENT.getDelayedQueue(),
                null);
        rabbitAdmin.declareBinding(bindingDelayedQueue);
    }

    private void bindingDlqQueue() {
        var bindingDlqQueue = new Binding(EMAIL_NOTIFICATION_PERSON_EVENT.getDlqQueue(),
                Binding.DestinationType.QUEUE,
                PERSON_DLQ_DIRECT_EXCHANGE.getProperty(),
                EMAIL_NOTIFICATION_PERSON_EVENT.getDlqQueue(),
                null);
        rabbitAdmin.declareBinding(bindingDlqQueue);
    }
}
