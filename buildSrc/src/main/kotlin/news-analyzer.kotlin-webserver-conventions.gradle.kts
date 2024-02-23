plugins {
    id("news-analyzer.kotlin-application-conventions")
}

val prometheus_version: String by project

dependencies {
    implementation("io.ktor:ktor-server-freemarker-jvm")
}