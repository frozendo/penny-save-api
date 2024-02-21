package com.frozendo.pennysave.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class DockerTestEnvironment {

    @Container
    static JdbcDatabaseContainer postgreSqlContainer = new PostgreSQLContainer("postgres:14-alpine")
            .withInitScript("schema.sql")
            .withDatabaseName("tests-db");

    @DynamicPropertySource
    static void testEnvironmentProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSqlContainer::getUsername);
        registry.add("spring.datasource.password", postgreSqlContainer::getPassword);
    }

}
