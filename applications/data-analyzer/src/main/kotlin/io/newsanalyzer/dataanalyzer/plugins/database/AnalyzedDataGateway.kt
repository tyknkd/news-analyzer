package io.newsanalyzer.dataanalyzer.plugins.database

import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.dataanalyzer.plugins.*
import io.newsanalyzer.datasupport.*

object AnalyzedDataGateway: AnalyzedArticlesGatewayTemplate, TopicsGatewayTemplate {
    suspend fun updateAll(): Boolean {
        val (articles, topics) = DataAnalyzer.getAnalyzedData()
        return if (upsertArticles(articles) && upsertTopics(topics)) {
            WebServerDataClient.postAnalyzedData(AnalyzedData(articles, topics))
        } else {
            false
        }
    }
}