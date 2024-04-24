package io.newsanalyzer.datacollector.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Table
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.datasupport.models.RawArticles
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import java.util.*
import kotlin.concurrent.timerTask

fun Application.configureDatabases(
    dbName: String = System.getenv("COLLECTOR_DB"),
    tables: List<Table> = listOf(RawArticles)): Database {
    val database = DatabaseTemplate(dbName, tables).database
    Timer().scheduleAtFixedRate(
        timerTask {
            log.info("Initiating data collection")
            runBlocking { CollectorDataGateway.updateArticles() }
        },
        0L,
        86400000L // daily
    )
    return database
}
