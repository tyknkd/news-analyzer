package io.newsanalyzer.datacollector.plugins.database

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.hours
import io.newsanalyzer.datacollector.models.*
import io.newsanalyzer.datacollector.plugins.DataCollector
import io.newsanalyzer.datacollector.plugins.database.CollectorDatabase.dbQuery


object ArticlesGateway: ArticlesDAO {
    fun init() {
        runBlocking {
            val latestDateTime = latestDateTime()
            if (latestDateTime == null || Clock.System.now().minus(latestDateTime) > 48.hours ) {
                val remoteData = DataCollector.collectData(latestDateTime)
                if (remoteData.totalResults > 0) {
                    addArticles(remoteData)
                }
            }
        }
    }
    private fun ResultRow.toArticle() = Article(
        id = this[Articles.id],
        publisher = this[Articles.publisher],
        author = this[Articles.author],
        title = this[Articles.title],
        description = this[Articles.description],
        url = this[Articles.url],
        urlToImage = this[Articles.urlToImage],
        publishedAt = this[Articles.publishedAt],
        content = this[Articles.content]
    )

    private suspend fun addArticles(remoteData: RemoteData) {
        dbQuery {
            Articles.batchInsert(data = remoteData.articles.reversed()) {
                (source, author, title, description, url,
                    urlToImage, publishedAt, content) ->
                this[Articles.publisher] = source.name
                this[Articles.author] = author
                this[Articles.title] = title
                this[Articles.description] = description
                this[Articles.url] = url
                this[Articles.urlToImage] = urlToImage
                this[Articles.publishedAt] = Instant.parse(publishedAt)
                this[Articles.content] = content
            }
        }
    }

    override suspend fun allArticles(): List<Article> = dbQuery {
        Articles.selectAll().map { row -> row.toArticle() }
    }

    override suspend fun latestDateTime(): Instant? = dbQuery {
        Articles.selectAll().lastOrNull()?.toArticle()?.publishedAt
    }
}