import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("news-analyzer.kotlin-webserver-conventions")
    id("news-analyzer.kotlin-client-conventions")
}

application {
    mainClass.set("io.newsanalyzer.webserver.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

(tasks.test) {
    testLogging {
        events(TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }
}