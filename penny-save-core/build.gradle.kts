plugins {
    id("org.flywaydb.flyway") version "9.22.1"
}

dependencies {
    implementation(project(":penny-save-person"))
    implementation(project(":penny-save-security"))
    implementation(project(":penny-save-shared"))
}

flyway {
    url = "jdbc:postgresql://localhost:5432/penny-save-db"
    schemas = arrayOf("public")
    user = "postgres"
    password = "root"
    cleanDisabled = false
}