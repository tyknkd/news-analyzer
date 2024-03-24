package io.newsanalyzer.dataanalyzer.plugins.database

import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.datasupport.models.*

object AnalyzerDatabase: DatabaseTemplate {
    override val envDbName = "ANALYZER_DB"
    override val tables = listOf(RawArticles, AnalyzedArticles, Topics)
}