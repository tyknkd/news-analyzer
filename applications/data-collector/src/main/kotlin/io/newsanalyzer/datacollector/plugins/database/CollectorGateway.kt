package io.newsanalyzer.datacollector.plugins.database

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datacollector.models.RemoteArticle
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

    private suspend fun addArticles(articles: List<RemoteArticle>): Boolean {
        var success = false
        dbQuery {
            val results = RawArticles.batchInsert(data = articles) {
                (source, author, title, description, url,
                    urlToImage, publishedAt, content) ->
                this[RawArticles.publisher] = source.name
                this[RawArticles.author] = author
                this[RawArticles.title] = title
                this[RawArticles.description] = description
                this[RawArticles.url] = url
                this[RawArticles.urlToImage] = urlToImage
                this[RawArticles.publishedAt] = Instant.parse(publishedAt)
                this[RawArticles.content] = content
                this[RawArticles.topicId] = -1
            }
            success = results.isNotEmpty()
        }
        return success
    }

    override suspend fun allArticles(): List<Article> = dbQuery {
        RawArticles.selectAll().map { row -> row.toArticle() }
    }

    override suspend fun articlesAfter(instant: Instant?): List<Article> {
        return if (instant == null ) {
            allArticles()
        } else {
            dbQuery{
                RawArticles.selectAll()
                    .where { RawArticles.publishedAt greater instant }
                    .map { row -> row.toArticle() }
            }
        }
    }

    override suspend fun latestDateTime(): Instant? = dbQuery {
        RawArticles.selectAll().lastOrNull()?.toArticle()?.publishedAt
    }

    override suspend fun updateArticles(): Boolean {
        val latestDateTime = latestDateTime()
        if (latestDateTime == null || Clock.System.now().minus(latestDateTime) > 24.hours ) {
            val latestPlusOne = latestDateTime?.plus(1.minutes)
            val articles = DataCollector.collectData(latestPlusOne)
            if (articles.isNullOrEmpty()) { return false
                } else {
                if (addArticles(articles)) {
                    return AnalyzerDataClient.postArticles(articlesAfter(latestDateTime))
                }
            }
        }
        return false
    }
}