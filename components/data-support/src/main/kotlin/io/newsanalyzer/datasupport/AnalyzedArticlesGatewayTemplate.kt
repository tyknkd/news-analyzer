package io.newsanalyzer.datasupport

import org.jetbrains.exposed.sql.*
import io.newsanalyzer.datasupport.models.*

interface AnalyzedArticlesGatewayTemplate: DataGatewayTemplate {
    fun ResultRow.toArticle() = Article(
        id = this[AnalyzedArticles.id],
        publisher = this[AnalyzedArticles.publisher],
        author = this[AnalyzedArticles.author],
        title = this[AnalyzedArticles.title],
        description = this[AnalyzedArticles.description],
        url = this[AnalyzedArticles.url],
        urlToImage = this[AnalyzedArticles.urlToImage],
        publishedAt = this[AnalyzedArticles.publishedAt],
        content = this[AnalyzedArticles.content],
        topicId = this[AnalyzedArticles.topicId]
    )

    suspend fun upsertArticles(articles: List<Article>): Boolean {
        var success = false
        dbQuery {
            val onUpdateExclude = AnalyzedArticles.columns - setOf(AnalyzedArticles.topicId)
            val results = AnalyzedArticles.batchUpsert(data = articles, onUpdateExclude = onUpdateExclude) {
                    (id, publisher, author, title, description, url,
                        urlToImage, publishedAt, content, topicId) ->
                this[AnalyzedArticles.id] = id
                this[AnalyzedArticles.publisher] = publisher
                this[AnalyzedArticles.author] = author
                this[AnalyzedArticles.title] = title
                this[AnalyzedArticles.description] = description
                this[AnalyzedArticles.url] = url
                this[AnalyzedArticles.urlToImage] = urlToImage
                this[AnalyzedArticles.publishedAt] = publishedAt
                this[AnalyzedArticles.content] = content
                this[AnalyzedArticles.topicId] = topicId
            }
            success = results.isNotEmpty()
        }
        return success
    }

    suspend fun allArticles(): List<Article> = dbQuery {
        AnalyzedArticles.selectAll().map { row -> row.toArticle() }.sortedBy { article -> article.id }
    }
}