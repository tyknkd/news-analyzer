package io.newsanalyzer.datacollector.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import io.newsanalyzer.datasupport.DatabaseClassTemplate
import io.newsanalyzer.datasupport.models.RawArticles

fun Application.configureDatabases(envDbName: String = "COLLECTOR_DB", tables: List<Table> = listOf(RawArticles)) {
    val collectorDatabase = DatabaseClassTemplate(envDbName, tables)
}
