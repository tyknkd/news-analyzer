package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.server.application.*
import io.newsanalyzer.dataanalyzer.plugins.database.AnalyzedDataGateway
import io.newsanalyzer.dataanalyzer.plugins.database.AnalyzerDatabase

fun Application.configureDatabases() {
    AnalyzerDatabase.init()
    AnalyzedDataGateway.init()
}
