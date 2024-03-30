package io.newsanalyzer.datacollector.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Table
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.datasupport.models.RawArticles
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases(
    envDbName: String = "COLLECTOR_DB",
    tables: List<Table> = listOf(RawArticles)): Database {
    val database = DatabaseTemplate(envDbName, tables).database
    runBlocking { CollectorDataGateway.updateArticles() }
    return database
}
