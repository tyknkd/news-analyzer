val kotlin_version = "1.9.22"
val ktor_version = "2.3.8"

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
}
