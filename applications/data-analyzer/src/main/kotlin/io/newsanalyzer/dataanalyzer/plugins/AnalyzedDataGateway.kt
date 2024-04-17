package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.client.*
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.*
import io.newsanalyzer.httpsupport.HttpClientTemplate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AnalyzedDataGateway:
    AnalyzedArticlesGatewayTemplate, TopicsGatewayTemplate {
    private var httpClient: HttpClient = HttpClientTemplate().httpClient
    fun updateClient(client: HttpClient) {
        httpClient = client
    }
    suspend fun updateAll(client: HttpClient = httpClient): Boolean {
        val (articles, topics) = DataAnalyzer.getAnalyzedData()
        return if (upsertArticles(articles) && upsertTopics(topics)) {
            val analyzedData = AnalyzedData(articles, topics)
            if (System.getenv("MQ_ENABLED").toBoolean()) {
                Messaging.publishMessage(Json.encodeToString(analyzedData))
            } else {
                WebServerDataClient.postAnalyzedData(analyzedData, client)
            }
        } else {
            false
        }
    }
}