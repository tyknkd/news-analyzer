package io.newsanalyzer.webserver.plugins.database

import io.newsanalyzer.databasesupport.DatabaseTemplate
import io.newsanalyzer.webserver.models.*

object WebDatabase: DatabaseTemplate {
    override val envDbName = "ANALYZER_DB"
    override val tables = listOf(Articles, Topics)
}