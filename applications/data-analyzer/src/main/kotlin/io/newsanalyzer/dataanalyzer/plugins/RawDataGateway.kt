package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.client.*
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.RawArticlesGatewayTemplate
import io.newsanalyzer.httpsupport.HttpClientTemplate


class RawDataGateway(
    val httpClient: HttpClient = HttpClientTemplate().httpClient):
    RawArticlesGatewayTemplate {
    suspend fun addArticles(articles: List<Article>, client: HttpClient = httpClient): Boolean {
        return if (upsertArticles(articles)) {
            AnalyzedDataGateway().updateAll(client)
        } else {
            false
        }
    }
}