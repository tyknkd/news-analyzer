package io.newsanalyzer.dataanalyzer.plugins.database

import io.newsanalyzer.dataanalyzer.models.*
import kotlinx.datetime.*

interface AnalyzedDAO {
    suspend fun allArticles(): List<Article>
    suspend fun allTopics(): List<Topic>
    suspend fun mostRecentDate(): Instant?
}