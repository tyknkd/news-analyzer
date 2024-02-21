package io.newsanalyzer.datacollector.plugins

import io.newsanalyzer.datacollector.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DataGateway(private val database: Database) {
    init {
        transaction(database) {
            SchemaUtils.create(Articles)
        }
    }

    private fun rowToArticle(row: ResultRow) = Article(
            id = row[Articles.id],
            publisher = row[Articles.publisher],
            author = row[Articles.author],
            title = row[Articles.title],
            description = row[Articles.description],
            url = row[Articles.url],
            urlToImage = row[Articles.urlToImage],
            publishedAt = row[Articles.publishedAt],
            content = row[Articles.content]
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
        Articles.selectAll().map(::rowToArticle)
    }
    suspend fun lastPublishedDate() {
        TODO()
    }
}