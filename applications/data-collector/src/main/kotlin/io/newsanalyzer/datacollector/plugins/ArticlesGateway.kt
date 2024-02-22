package io.newsanalyzer.datacollector.plugins

import io.newsanalyzer.datacollector.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DataGateway {
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

    private suspend fun <T> dbQuery(block: suspend() -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

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
    suspend fun allArticles(): List<Article> = dbQuery {
        Articles.selectAll().map { row -> row.toArticle() }
    }
    // ::rowToArticle
    suspend fun mostRecentDate(): Instant? = dbQuery {
        Articles.selectAll().lastOrNull()?.toArticle()?.publishedAt
    }
}