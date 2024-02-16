plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

val kotlin_version: String by project
val ktor_version: String by project
val kover_version: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    implementation("io.ktor.plugin:plugin:$ktor_version")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:$kover_version")
}
