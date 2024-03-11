package io.newsanalyzer.datacollector.plugins

import io.ktor.server.application.*

fun Application.configureDatabases() {
    CollectorDatabase.init()
}
