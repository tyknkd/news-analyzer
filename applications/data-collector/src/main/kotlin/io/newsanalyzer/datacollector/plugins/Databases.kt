package io.newsanalyzer.datacollector.plugins

import io.ktor.server.application.*
import io.newsanalyzer.datacollector.plugins.database.ArticlesGateway
import io.newsanalyzer.datacollector.plugins.database.CollectorDatabase

fun Application.configureDatabases() {
    CollectorDatabase.init()
    ArticlesGateway.init()
}
