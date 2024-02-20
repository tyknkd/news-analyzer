plugins {
    id("news-analyzer.kotlin-application-conventions")
}

application {
    mainClass.set("io.newsanalyzer.webserver.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}