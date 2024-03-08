package com.pennysave.frozendo.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

public class DockerTestEnvironment implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer("postgres:14-alpine")
            .withDatabaseName("test-db");

    private final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.13.0-alpine")
            .withExposedPorts(5672, 15672);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        postgreSqlContainer.withExposedPorts(5432);
        postgreSqlContainer.withInitScript("init-database.sql");
        postgreSqlContainer.start();

        rabbitMQContainer.withExposedPorts(5672);
        rabbitMQContainer.start();

        var databaseUrl = "spring.datasource.url=" + postgreSqlContainer.getJdbcUrl();
        var username = "spring.datasource.username=" + postgreSqlContainer.getUsername();
        var password = "spring.datasource.password=" + postgreSqlContainer.getPassword();

        String rabbitHost = "spring.rabbitmq.host= " + rabbitMQContainer.getHost();
        String rabbitPort = "spring.rabbitmq.port= " + rabbitMQContainer.getAmqpPort();
        String rabbitUsername = "spring.rabbitmq.username= " + rabbitMQContainer.getAdminUsername();
        String rabbitPassword = "spring.rabbitmq.password= " + rabbitMQContainer.getAdminPassword();

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, databaseUrl, username, password,
                rabbitHost, rabbitPort, rabbitUsername, rabbitPassword);

    }

}
