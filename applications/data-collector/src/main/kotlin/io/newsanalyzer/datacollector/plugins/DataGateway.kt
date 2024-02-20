package io.newsanalyzer.datacollector.plugins

import io.newsanalyzer.datacollector.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DataGateway(private val database: Database) {
    init {
        transaction(database) {
            SchemaUtils.create(Articles)
        }
    }
    suspend fun <T> dbQuery(block: suspend() -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun import(remoteData: RemoteData) {
        for (article in remoteData.articles) {
            Articles.insert {
                it[publisher] = article.source.name
                it[author] = article.author
                it[title] = article.title
                it[description] = article.description
                it[url] = article.url
                it[urlToImage] = article.urlToImage
                it[publishedAt] = article.publishedAt.toInstant()
                it[content] = article.content
            }[Articles.id]
        }
    }
}