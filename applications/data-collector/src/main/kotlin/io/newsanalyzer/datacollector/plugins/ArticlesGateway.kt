package io.newsanalyzer.datacollector.plugins

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import io.newsanalyzer.datacollector.models.*
import io.newsanalyzer.datacollector.plugins.CollectorDatabase.dbQuery


class ArticlesGateway: ArticlesDAO {
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

    suspend fun addArticles(remoteData: RemoteData) {
        for (article in remoteData.articles.reversed()) {
            dbQuery {
                Articles.insert {
                    it[publisher] = article.source.name
                    it[author] = article.author
                    it[title] = article.title
                    it[description] = article.description
                    it[url] = article.url
                    it[urlToImage] = article.urlToImage
                    it[publishedAt] = article.publishedAt.toInstant()
                    it[content] = article.content
                }
            }
        }
    }

    override suspend fun allArticles(): List<Article> = dbQuery {
        Articles.selectAll().map { row -> row.toArticle() }
    }

    override suspend fun mostRecentDate(): Instant? = dbQuery {
        Articles.selectAll().lastOrNull()?.toArticle()?.publishedAt
    }
}

val articlesGateway: ArticlesDAO = ArticlesGateway().apply {
    runBlocking {
        if(allArticles().isEmpty()) {
            val dataCollector = DataCollector()
            val remoteData = dataCollector.collectData()
            addArticles(remoteData)
        }
    }
}