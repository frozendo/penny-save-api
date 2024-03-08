package com.frozendo.pennysave.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class DockerTestEnvironment {

    @Container
    static JdbcDatabaseContainer postgreSqlContainer = new PostgreSQLContainer("postgres:14-alpine")
            .withInitScript("init-database.sql")
            .withDatabaseName("tests-db");

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.13.0-management-alpine")
            .withExposedPorts(5672, 15672);

    @DynamicPropertySource
    static void testEnvironmentProperties(DynamicPropertyRegistry registry) {
        registryDatabaseProperties(registry);
        registryRabbitMQProperties(registry);
    }

    private static void registryDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSqlContainer::getUsername);
        registry.add("spring.datasource.password", postgreSqlContainer::getPassword);
    }

    private static void registryRabbitMQProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

}
