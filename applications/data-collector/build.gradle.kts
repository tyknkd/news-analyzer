plugins {
    id("news-analyzer.kotlin-application-conventions")
}

val ktor_version: String by project

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-java:$ktor_version")
}

application {
    mainClass.set("com.example.datacollector.ApplicationKt")
}