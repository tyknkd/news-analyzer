package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import com.example.plugins.*

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
