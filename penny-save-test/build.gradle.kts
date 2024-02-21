import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java-test-fixtures")
}

dependencies {
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesImplementation("org.springframework.boot:spring-boot-testcontainers")
    testFixturesImplementation("org.testcontainers:junit-jupiter")
    testFixturesImplementation("org.testcontainers:postgresql")
    testFixturesImplementation("io.rest-assured:spring-mock-mvc:5.1.1")
}

tasks.withType<BootJar> {
    enabled = false
}

tasks.jar {
    enabled = true
}