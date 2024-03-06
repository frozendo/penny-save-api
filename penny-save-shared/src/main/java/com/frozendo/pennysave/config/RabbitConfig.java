package com.frozendo.pennysave.config;

import com.frozendo.pennysave.config.properties.RabbitProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    private final RabbitProperties rabbitProperties;

    public RabbitConfig(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    public ConnectionFactory connectionFactory() {
        var connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitProperties.host());
        connectionFactory.setPort(rabbitProperties.port());
        connectionFactory.setUsername(rabbitProperties.username());
        connectionFactory.setPassword(rabbitProperties.password());
        connectionFactory.setVirtualHost("/");

        return connectionFactory;
    }

    @Bean
    public RabbitTemplate createRabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public RabbitAdmin createRabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

}
