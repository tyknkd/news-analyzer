plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
}

group = "io.newsanalyzer"
version = "0.0.1"

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

dependencies {
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks.test {
    failFast = true
}