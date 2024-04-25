plugins {
    id("news-analyzer.kotlin-common-conventions")
}

val micrometer_prometheus_version: String by project

dependencies {
    implementation(project(":components:mq-support"))
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-resources")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometer_prometheus_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
}