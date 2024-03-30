package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.client.*
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.*
import io.newsanalyzer.httpsupport.HttpClientTemplate

object AnalyzedDataGateway:
    AnalyzedArticlesGatewayTemplate, TopicsGatewayTemplate {
    private var httpClient: HttpClient = HttpClientTemplate().httpClient
    fun updateClient(client: HttpClient) {
        httpClient = client
    }
    suspend fun updateAll(client: HttpClient = httpClient): Boolean {
        val (articles, topics) = DataAnalyzer.getAnalyzedData()
        return if (upsertArticles(articles) && upsertTopics(topics)) {
            WebServerDataClient.postAnalyzedData(AnalyzedData(articles, topics), client)
        } else {
            false
        }
    }
}