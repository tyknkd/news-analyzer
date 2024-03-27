plugins {
    id("news-analyzer.kotlin-dataserver-conventions")
    id("news-analyzer.kotlin-httpclient-conventions")
}

application {
    mainClass.set("io.newsanalyzer.datacollector.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}