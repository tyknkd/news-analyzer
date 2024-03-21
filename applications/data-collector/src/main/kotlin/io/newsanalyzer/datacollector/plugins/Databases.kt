package io.newsanalyzer.datacollector.plugins

import io.ktor.server.application.*
import io.newsanalyzer.datacollector.plugins.database.CollectorGateway
import io.newsanalyzer.datacollector.plugins.database.CollectorDatabase

fun Application.configureDatabases() {
    CollectorDatabase.init()
    CollectorGateway.init()
}
