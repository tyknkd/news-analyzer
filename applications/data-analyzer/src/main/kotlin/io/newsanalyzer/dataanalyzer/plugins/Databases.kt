package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.server.application.*

fun Application.configureDatabases() {
    AnalyzerDatabase.init()
    AnalysisGateway.init()
}
