package io.newsanalyzer.dataanalyzer.plugins.database

import io.newsanalyzer.dataanalyzer.models.*
import kotlinx.datetime.*

interface RawDAO {
    suspend fun addArticles(articles: List<Article>): Boolean
    suspend fun allArticles(): List<Article>
    suspend fun mostRecentDate(): Instant?
}