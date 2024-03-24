package io.newsanalyzer.dataanalyzer.plugins.database

import io.newsanalyzer.databasesupport.DatabaseTemplate
import io.newsanalyzer.dataanalyzer.models.*

object AnalyzerDatabase: DatabaseTemplate {
    override val envDbName = "ANALYZER_DB"
    override val tables = listOf(RawArticles, AnalyzedArticles, Topics)
}