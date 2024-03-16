package io.newsanalyzer.webserver.plugins

import io.ktor.server.application.*
import io.newsanalyzer.webserver.plugins.database.AnalyzedDataGateway
import io.newsanalyzer.webserver.plugins.database.AnalyzedDatabase

fun Application.configureDatabases() {
    AnalyzedDatabase.init()
    AnalyzedDataGateway.init()
}
