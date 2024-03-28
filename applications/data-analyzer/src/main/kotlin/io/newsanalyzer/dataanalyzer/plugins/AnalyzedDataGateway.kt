package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.client.*
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.*
import io.newsanalyzer.httpsupport.HttpClientTemplate

class AnalyzedDataGateway(
    val httpClient: HttpClient = HttpClientTemplate().httpClient):
    AnalyzedArticlesGatewayTemplate, TopicsGatewayTemplate {
    suspend fun updateAll(client: HttpClient = httpClient): Boolean {
        val (articles, topics) = DataAnalyzer.getAnalyzedData()
        return if (upsertArticles(articles) && upsertTopics(topics)) {
            WebServerDataClient.postAnalyzedData(AnalyzedData(articles, topics), client)
        } else {
            false
        }
    }
}