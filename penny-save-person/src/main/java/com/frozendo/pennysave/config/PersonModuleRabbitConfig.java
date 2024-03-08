package com.frozendo.pennysave.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Configuration;

import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_DIRECT_EXCHANGE;
import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_DLQ_DIRECT_EXCHANGE;

@Configuration
public class PersonModuleRabbitConfig {

    private final RabbitAdmin rabbitAdmin;

    public PersonModuleRabbitConfig(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @PostConstruct
    public void personModuleRabbitComponents() {
        createPersonExchange();
        createPersonDlqExchange();
    }

    private void createPersonExchange() {
        var personExchange = ExchangeBuilder
                .directExchange(PERSON_DIRECT_EXCHANGE.getProperty())
                .build();
        rabbitAdmin.declareExchange(personExchange);
    }

    private void createPersonDlqExchange() {
        var personExchange = ExchangeBuilder
                .directExchange(PERSON_DLQ_DIRECT_EXCHANGE.getProperty())
                .build();
        rabbitAdmin.declareExchange(personExchange);
    }
}
