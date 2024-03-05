import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.frozendo"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

allprojects {

	repositories {
		mavenCentral()
	}

}

subprojects {
	apply(plugin = "java")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	dependencies {
		implementation("org.springframework.boot:spring-boot-starter-data-jpa")
		implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("org.springframework.boot:spring-boot-starter-validation")
		implementation("org.springframework.boot:spring-boot-starter-security")
		implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

		runtimeOnly("org.postgresql:postgresql")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation(testFixtures(project(":penny-save-test")))

		testImplementation("io.rest-assured:spring-mock-mvc:5.3.2")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

}

tasks.withType<BootJar> {
	enabled = false
}

tasks.jar {
	enabled = true
}
