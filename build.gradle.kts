import java.util.Properties

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    application
}

group = "dev.nsemiklit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Загрузка local.properties
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    // Простые зависимости для HTTP клиента
    implementation("io.ktor:ktor-client-core:3.2.3")
    implementation("io.ktor:ktor-client-okhttp:3.2.3")
    implementation("io.ktor:ktor-client-content-negotiation:3.2.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("dev.nsemiklit.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// Передача свойств в runtime
tasks.withType<JavaExec> {
    systemProperty("openai.api.key", localProperties.getProperty("openai.api.key", ""))
    systemProperty("openai.base.url", localProperties.getProperty("openai.base.url", "https://api.proxyapi.ru/openai/v1"))
    systemProperty("openai.model", localProperties.getProperty("openai.model", "gpt-5-mini"))
}
