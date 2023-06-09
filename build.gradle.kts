import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val slack_api_version: String by project
val ktor_version: String by project

plugins {
    kotlin("jvm") version "1.8.21"
    application
    kotlin("plugin.serialization").version("1.6.21")
}

group = "org.lunatech"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.slack.api:bolt:$slack_api_version")
    implementation("com.slack.api:bolt-servlet:$slack_api_version")
    implementation("com.slack.api:bolt-jetty:$slack_api_version")
    implementation("org.slf4j:slf4j-simple:1.7.36")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}