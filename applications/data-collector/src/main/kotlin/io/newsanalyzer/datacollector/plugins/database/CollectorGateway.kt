package io.newsanalyzer.datacollector.plugins.database

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import io.newsanalyzer.datacollector.models.*
import io.newsanalyzer.datacollector.plugins.AnalyzerDataClient
import io.newsanalyzer.datacollector.plugins.DataCollector
import io.newsanalyzer.datacollector.plugins.database.CollectorDatabase.dbQuery


object CollectorGateway: CollectorDAO {
    fun init() {
        runBlocking {
            updateArticles()
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

    private suspend fun addArticles(remoteData: RemoteData): Boolean {
        var success = false
        dbQuery {
            val results = Articles.batchInsert(data = remoteData.articles.reversed()) {
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
            success = results.isNotEmpty()
        }
        return success
    }

    override suspend fun allArticles(): List<Article> = dbQuery {
        Articles.selectAll().map { row -> row.toArticle() }
    }

    override suspend fun articlesAfter(instant: Instant?): List<Article> {
        return if (instant == null ) {
            allArticles()
        } else {
            dbQuery{
                Articles.selectAll()
                    .where { Articles.publishedAt greater instant }
                    .map { row -> row.toArticle() }
            }
        }
    }

    override suspend fun latestDateTime(): Instant? = dbQuery {
        Articles.selectAll().lastOrNull()?.toArticle()?.publishedAt
    }

    override suspend fun updateArticles(): Boolean {
        val latestDateTime = latestDateTime()
        if (latestDateTime == null || Clock.System.now().minus(latestDateTime) > 24.hours ) {
            val latestPlusOne = latestDateTime?.plus(1.minutes)
            val remoteData = DataCollector.collectData(latestPlusOne)
            if (remoteData.totalResults > 0) {
                if (addArticles(remoteData)) {
                    return AnalyzerDataClient.postArticles(articlesAfter(latestDateTime))
                }
            }
        }
        return false
    }
}