package com.frozendo.pennysave.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frozendo.pennysave.config.properties.RabbitProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

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
    public Jackson2JsonMessageConverter createJackson2JsonMessageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate createRabbitTemplate(ObjectMapper mapper) {
        var rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(createJackson2JsonMessageConverter(mapper));
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin createRabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

}
