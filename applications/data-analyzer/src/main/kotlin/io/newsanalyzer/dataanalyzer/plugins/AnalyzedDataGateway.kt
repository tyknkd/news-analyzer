package io.newsanalyzer.dataanalyzer.plugins

import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AnalyzedDataGateway:
    AnalyzedArticlesGatewayTemplate, TopicsGatewayTemplate {
    suspend fun updateAll(): Boolean {
        val (articles, topics) = DataAnalyzer.getAnalyzedData()
        return if (upsertArticles(articles) && upsertTopics(topics)) {
            val analyzedData = AnalyzedData(articles, topics)
            Messaging.analyzerMessenger.publishMessage(Json.encodeToString(analyzedData))
        } else {
            false
        }
    }
}