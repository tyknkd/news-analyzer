package io.newsanalyzer.datacollector.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Table
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.datasupport.models.RawArticles

fun Application.configureDatabases(
    envDbName: String = "COLLECTOR_DB",
    tables: List<Table> = listOf(RawArticles)) {
    DatabaseTemplate(envDbName, tables)
}
