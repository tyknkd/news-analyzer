package io.newsanalyzer.datacollector

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.newsanalyzer.datacollector.plugins.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() {
    val port = System.getenv("COLLECTOR_PORT")?.toInt() ?: 8081
    embeddedServer(factory = Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureRouting()
}
