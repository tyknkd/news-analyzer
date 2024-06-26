package io.newsanalyzer.webserver

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.newsanalyzer.webserver.plugins.*
import java.util.*

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    Messaging.analyzerMessenger.listen()
    val port = System.getenv("WEBSERVER_PORT")?.toInt() ?: 8888
    embeddedServer(factory = Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureDatabases()
    configureTemplating()
    configureRouting()
}
