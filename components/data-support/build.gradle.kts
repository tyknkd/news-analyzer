import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("news-analyzer.kotlin-database-conventions")
    id("news-analyzer.kotlin-client-conventions")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

(tasks.test) {
    testLogging {
        events(TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }
}