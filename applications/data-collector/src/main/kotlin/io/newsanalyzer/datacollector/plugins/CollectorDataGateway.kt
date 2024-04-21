package io.newsanalyzer.datacollector.plugins

import io.ktor.client.*
import org.jetbrains.exposed.sql.*
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.RawArticlesGatewayTemplate
import io.newsanalyzer.httpsupport.HttpClientTemplate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CollectorDataGateway: RawArticlesGatewayTemplate {
    private var httpClient: HttpClient = HttpClientTemplate().httpClient
    fun updateClient(client: HttpClient) {
        httpClient = client
    }

    suspend fun updateArticles(client: HttpClient = httpClient): Boolean {
        val latestDateTime = latestDateTime()
        if (latestDateTime == null || Clock.System.now().minus(latestDateTime) > 24.hours ) {
            val latestPlusOne = latestDateTime?.plus(1.minutes)
            val remoteArticles = DataCollector.collectData(latestPlusOne, client)
            if (remoteArticles.isNullOrEmpty()) { return false
            } else {
                if (addRemoteArticles(remoteArticles)) {
                    val newArticles = articlesAfter(latestDateTime)
                    return Messaging.messenger.publishMessage(Json.encodeToString(newArticles))
                }
            }
        }
        return false
    }

    suspend fun addRemoteArticles(remoteArticles: List<RemoteArticle>): Boolean {
        var success = false
        dbQuery {
            val results = RawArticles.batchInsert(data = remoteArticles) {
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

    suspend fun articlesAfter(instant: Instant?): List<Article> {
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

    suspend fun latestDateTime(): Instant? = dbQuery {
        RawArticles.selectAll().lastOrNull()?.toArticle()?.publishedAt
    }
}