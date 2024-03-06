package com.frozendo.pennysave.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.rabbitmq")
public record RabbitProperties(
        String host,
        String username,
        String password,
        int port
) {
}
