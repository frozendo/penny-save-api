import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java-test-fixtures")
}

dependencies {
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

tasks.withType<BootJar> {
    enabled = false
}

tasks.jar {
    enabled = true
}