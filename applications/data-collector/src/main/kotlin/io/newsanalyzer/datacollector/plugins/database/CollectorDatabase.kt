package io.newsanalyzer.datacollector.plugins.database

import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.datasupport.models.RawArticles

object CollectorDatabase: DatabaseTemplate {
    override val envDbName = "COLLECTOR_DB"
    override val tables = listOf(RawArticles)
}