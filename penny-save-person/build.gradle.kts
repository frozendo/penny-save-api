import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation(project(":penny-save-shared"))
    implementation(project(":penny-save-security"))

    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
}

tasks.withType<BootJar> {
    enabled = false
}

tasks.jar {
    enabled = true
}