package io.newsanalyzer.datacollector

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*
import io.newsanalyzer.datacollector.plugins.*

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("COLLECTOR_PORT")?.toInt() ?: 8886
    embeddedServer(factory = Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
