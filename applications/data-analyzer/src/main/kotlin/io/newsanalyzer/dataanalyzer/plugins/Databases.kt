package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Table
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.datasupport.models.*

fun Application.configureDatabases(
    envDbName: String = "ANALYZER_DB",
    tables: List<Table> = listOf(RawArticles, AnalyzedArticles, Topics)) {
    DatabaseTemplate(envDbName, tables)
}
