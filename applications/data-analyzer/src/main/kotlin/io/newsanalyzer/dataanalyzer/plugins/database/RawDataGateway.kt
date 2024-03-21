package io.newsanalyzer.dataanalyzer.plugins.database

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import io.newsanalyzer.dataanalyzer.models.*
import io.newsanalyzer.dataanalyzer.plugins.CollectedDataClient
import io.newsanalyzer.dataanalyzer.plugins.database.AnalyzerDatabase.dbQuery


object RawDataGateway: RawDAO {
    fun init() {
        runBlocking {
            if(allArticles().isEmpty()) {
                val articles = CollectedDataClient.getCollectedData()
                addArticles(articles)
            }
        }
    }

    private fun ResultRow.toArticle() = Article(
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

    private suspend fun upsertArticles(articles: List<Article>) {
        dbQuery {
            val onUpdateExclude = (RawArticles.columns.toSet() - RawArticles.topicId).toList()
            RawArticles.batchUpsert(data = articles, onUpdateExclude = onUpdateExclude) {
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
        }
    }

    override suspend fun addArticles(articles: List<Article>): Boolean {
        upsertArticles(articles)
        return true
    }

    override suspend fun allArticles(): List<Article> = dbQuery {
        RawArticles.selectAll().map { row -> row.toArticle() }
    }

    override suspend fun mostRecentDate(): Instant? = dbQuery {
        RawArticles.selectAll().lastOrNull()?.toArticle()?.publishedAt
    }
}