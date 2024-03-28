package io.newsanalyzer.webserver.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Table
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.datasupport.models.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases(
    envDbName: String = "WEBSERVER_DB",
    tables: List<Table> = listOf(AnalyzedArticles, Topics)): Database {
    return DatabaseTemplate(envDbName, tables).database
}
