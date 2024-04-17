package io.newsanalyzer.dataanalyzer

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*
import io.newsanalyzer.dataanalyzer.plugins.*

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    if (System.getenv("MQ_ENABLED").toBoolean()) {
        Messaging.listen()
    }
    val port = System.getenv("ANALYZER_PORT")?.toInt() ?: 8887
    embeddedServer(factory = Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureRouting()
}
