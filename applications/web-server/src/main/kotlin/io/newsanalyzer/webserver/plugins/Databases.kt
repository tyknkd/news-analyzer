package io.newsanalyzer.webserver.plugins

import io.ktor.server.application.*

fun Application.configureDatabases() {
    AnalyzedDatabase.init()
    AnalyzedDataGateway.init()
}
