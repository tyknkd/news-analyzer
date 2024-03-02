plugins {
    id("news-analyzer.kotlin-dataserver-conventions")
    id("news-analyzer.kotlin-client-conventions")
}

application {
    mainClass.set("io.newsanalyzer.dataanalyzer.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}