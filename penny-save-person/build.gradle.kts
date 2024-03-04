import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation(project(":penny-save-shared"))
}

tasks.withType<BootJar> {
    enabled = false
}

tasks.jar {
    enabled = true
}