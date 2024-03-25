package io.newsanalyzer.datasupport.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.*

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
    val content: String,
    val topicId: Int = -1
)

open class ArticlesTemplate : Table() {
    val id = integer("id").autoIncrement()
    val publisher = varchar("publisher", 64)
    val author = varchar("author", 128)
    val title = varchar("title", 256)
    val description = varchar("description", 512)
    val url = varchar("url", 256)
    val urlToImage = varchar("urlToImage", 512)
    val publishedAt = timestamp("publishedAt")
    val content = varchar("content", 512)
    val topicId = integer("topicId")
    override val primaryKey = PrimaryKey(id)
}

object AnalyzedArticles: ArticlesTemplate()
object RawArticles: ArticlesTemplate()