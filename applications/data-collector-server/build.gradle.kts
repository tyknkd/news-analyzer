plugins {
    id("news-analyzer.kotlin-application-conventions")
    id("news-analyzer.kotlin-client-conventions")
}

dependencies {
    implementation(project(":components:data-collector"))
}

application {
    mainClass.set("com.example.datacollector.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}