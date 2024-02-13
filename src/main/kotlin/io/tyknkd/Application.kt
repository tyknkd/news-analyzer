package io.tyknkd

import io.ktor.server.application.*
import io.tyknkd.plugins.*

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
