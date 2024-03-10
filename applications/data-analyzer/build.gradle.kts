plugins {
    id("news-analyzer.kotlin-dataanalyzer-conventions")
    id("news-analyzer.kotlin-client-conventions")
}

application {
    mainClass.set("io.newsanalyzer.dataanalyzer.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

(tasks.shadowJar) {
    isZip64 = true
}
