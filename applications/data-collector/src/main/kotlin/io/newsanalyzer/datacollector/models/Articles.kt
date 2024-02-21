package io.newsanalyzer.datacollector.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

object Articles: Table() {
    val id = integer("id").autoIncrement()
    val publisher = varchar("publisher", 64)
    val author = varchar("author", 128)
    val title = varchar("title", 256)
    val description = varchar("description", 512)
    val url = varchar("url", 256)
    val urlToImage = varchar("urlToImage", 512)
    val publishedAt = timestamp("publishedAt")
    val content = varchar("content", 512)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Article(
    val id: Int,
    val publisher: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: Instant,
    val content: String
)