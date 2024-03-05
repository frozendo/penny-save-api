import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

tasks.withType<BootJar> {
    enabled = false
}

tasks.jar {
    enabled = true
}