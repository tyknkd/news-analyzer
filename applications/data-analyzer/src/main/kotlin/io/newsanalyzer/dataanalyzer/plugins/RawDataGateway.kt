package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.client.*
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.RawArticlesGatewayTemplate
import io.newsanalyzer.httpsupport.HttpClientTemplate


object RawDataGateway: RawArticlesGatewayTemplate {
    private var httpClient: HttpClient = HttpClientTemplate().httpClient
    fun updateClient(client: HttpClient) {
        httpClient = client
    }
    suspend fun addArticles(articles: List<Article>, client: HttpClient = httpClient): Boolean {
        return if (upsertArticles(articles)) {
            AnalyzedDataGateway.updateAll(client)
        } else {
            false
        }
    }
}