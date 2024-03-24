package io.newsanalyzer.datasupport

import org.jetbrains.exposed.sql.*
import io.newsanalyzer.datasupport.models.*

interface RawArticlesGatewayTemplate: DataGatewayTemplate {
    fun ResultRow.toArticle() = Article(
        id = this[RawArticles.id],
        publisher = this[RawArticles.publisher],
        author = this[RawArticles.author],
        title = this[RawArticles.title],
        description = this[RawArticles.description],
        url = this[RawArticles.url],
        urlToImage = this[RawArticles.urlToImage],
        publishedAt = this[RawArticles.publishedAt],
        content = this[RawArticles.content],
        topicId = this[RawArticles.topicId]
    )

    suspend fun upsertArticles(articles: List<Article>): Boolean {
        var success = false
        dbQuery {
            val onUpdateExclude = RawArticles.columns - setOf(RawArticles.topicId)
            val results = RawArticles.batchUpsert(data = articles, onUpdateExclude = onUpdateExclude) {
                    (id, publisher, author, title, description, url,
                        urlToImage, publishedAt, content, topicId) ->
                this[RawArticles.id] = id
                this[RawArticles.publisher] = publisher
                this[RawArticles.author] = author
                this[RawArticles.title] = title
                this[RawArticles.description] = description
                this[RawArticles.url] = url
                this[RawArticles.urlToImage] = urlToImage
                this[RawArticles.publishedAt] = publishedAt
                this[RawArticles.content] = content
                this[RawArticles.topicId] = topicId
            }
            success = results.isNotEmpty()
        }
        return success
    }

    suspend fun allArticles(): List<Article> = dbQuery {
        RawArticles.selectAll().map { row -> row.toArticle() }
    }
}