package io.newsanalyzer.webserver

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.newsanalyzer.webserver.plugins.*

fun main() {
    val port = System.getenv("WEBSERVER_PORT")?.toInt() ?: 8080
    embeddedServer(factory = Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureTemplating()
    configureDatabases()
    configureRouting()
}
