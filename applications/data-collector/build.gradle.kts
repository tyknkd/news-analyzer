plugins {
    id("news-analyzer.kotlin-application-conventions")
    id("news-analyzer.kotlin-client-conventions")
}

application {
    mainClass.set("io.newsanalyzer.datacollector.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}