package io.newsanalyzer.datacollector.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Table
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.datasupport.models.RawArticles
import kotlinx.coroutines.runBlocking

fun Application.configureDatabases() {
    val tables: List<Table> = listOf(RawArticles)
    DatabaseTemplate("COLLECTOR_DB", tables)
    runBlocking { CollectorDataGateway.updateArticles() }
}
