package com.example.datacollector

import com.example.datacollector.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() {
    runBlocking {
        launch {
            val collector = Collector()
            collector.collectData()
        }
    }
    val port = System.getenv("COLLECTOR_PORT")?.toInt() ?: 8081
    embeddedServer(factory = Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureRouting()
}
