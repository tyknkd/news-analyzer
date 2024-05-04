plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
}

group = "io.newsanalyzer"
version = "0.0.1"

val jvm_version: String by project

kotlin {
    jvmToolchain(jvm_version.toInt())
}

repositories {
    mavenCentral()
}

val ktor_version: String by project
val kotlin_version: String by project
val kotlinx_coroutines_version: String by project
val kotlinx_datetime_version: String by project
val logback_version: String by project
val awaitility_version: String by project

dependencies {
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinx_datetime_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinx_coroutines_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.awaitility:awaitility-kotlin:$awaitility_version")
}

tasks.test {
    failFast = true
}