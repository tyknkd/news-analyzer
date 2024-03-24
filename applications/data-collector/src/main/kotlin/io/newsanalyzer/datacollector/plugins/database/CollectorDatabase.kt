package io.newsanalyzer.datacollector.plugins.database

import io.newsanalyzer.databasesupport.DatabaseTemplate
import io.newsanalyzer.datacollector.models.Articles

object CollectorDatabase: DatabaseTemplate {
    override val envDbName = "COLLECTOR_DB"
    override val tables = listOf(Articles)
}