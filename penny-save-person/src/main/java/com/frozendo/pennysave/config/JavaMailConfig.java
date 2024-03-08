package com.frozendo.pennysave.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Profile("test")
@Configuration
public class JavaMailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        var javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setProtocol("smtp");
        javaMailSender.setHost("127.0.0.1");
        javaMailSender.setPort(3025);
        javaMailSender.setUsername("penny-save-mail");
        javaMailSender.setPassword("mailpassword");
        return javaMailSender;
    }

}
