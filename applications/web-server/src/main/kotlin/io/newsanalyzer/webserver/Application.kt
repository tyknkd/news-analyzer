package io.newsanalyzer.webserver

import io.ktor.server.application.*
import io.newsanalyzer.webserver.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureTemplating()
    configureDatabases()
    configureRouting()
}
