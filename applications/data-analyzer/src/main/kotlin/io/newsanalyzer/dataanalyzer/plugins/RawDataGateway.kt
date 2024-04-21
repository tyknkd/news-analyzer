package io.newsanalyzer.dataanalyzer.plugins

import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.RawArticlesGatewayTemplate

object RawDataGateway: RawArticlesGatewayTemplate {
    suspend fun addArticles(articles: List<Article>): Boolean {
        return if (upsertArticles(articles)) {
            AnalyzedDataGateway.updateAll()
        } else {
            false
        }
    }
}