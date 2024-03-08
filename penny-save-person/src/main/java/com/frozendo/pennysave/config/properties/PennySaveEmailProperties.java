package com.frozendo.pennysave.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.email")
public record PennySaveEmailProperties(
        String from,
        String name,
        String subject,
        String link
) {
}
