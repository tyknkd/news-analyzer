package io.newsanalyzer.datacollector.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.*

object Articles : Table() {
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