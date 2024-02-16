val kotlin_version = "1.9.22"
val ktor_version = "2.3.8"
val kover_version = "0.7.5"

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    implementation("io.ktor.plugin:plugin:$ktor_version")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:$kover_version")
}
