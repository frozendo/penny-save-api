package com.frozendo.pennysave.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordEncryptionConfig {

    @Bean
    public BCryptPasswordEncoder createEncryptPassword() {
        return new BCryptPasswordEncoder();
    }

}
