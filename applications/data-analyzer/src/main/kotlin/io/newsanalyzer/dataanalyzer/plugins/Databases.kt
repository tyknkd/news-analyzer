package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.server.application.*
import io.newsanalyzer.dataanalyzer.plugins.database.AnalysisGateway
import io.newsanalyzer.dataanalyzer.plugins.database.AnalyzerDatabase

fun Application.configureDatabases() {
    AnalyzerDatabase.init()
    AnalysisGateway.init()
}
