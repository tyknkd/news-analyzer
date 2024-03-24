package io.newsanalyzer.webserver.plugins.database

import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.datasupport.models.*

object WebDatabase: DatabaseTemplate {
    override val envDbName = "WEBSERVER_DB"
    override val tables = listOf(AnalyzedArticles, Topics)
}