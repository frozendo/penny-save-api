package com.frozendo.pennysave.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Configuration;

import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_CREATE_KEY;
import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_DIRECT_EXCHANGE;

@Configuration
public class PersonModuleRabbitConfig {

    private final RabbitAdmin rabbitAdmin;

    public PersonModuleRabbitConfig(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @PostConstruct
    public void personModuleRabbitComponents() {
        createPersonExchange();
        createTest();
    }

    private void createPersonExchange() {
        var personExchange = ExchangeBuilder
                .directExchange(PERSON_DIRECT_EXCHANGE.getProperty())
                .build();
        rabbitAdmin.declareExchange(personExchange);
    }

    private void createTest() {
        var queue = QueueBuilder.durable("test-queue").build();
        rabbitAdmin.declareQueue(queue);

        var binding = new Binding("test-queue", Binding.DestinationType.QUEUE, PERSON_DIRECT_EXCHANGE.getProperty(), PERSON_CREATE_KEY.getProperty(), null);
        rabbitAdmin.declareBinding(binding);
    }
}
