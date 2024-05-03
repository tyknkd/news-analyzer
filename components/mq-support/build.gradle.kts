import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("news-analyzer.kotlin-messagequeue-conventions")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation(project(":components:test-support"))
}

(tasks.test) {
    testLogging {
        events(TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }
}