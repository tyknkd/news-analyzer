package io.newsanalyzer.dataanalyzer.plugins

import io.newsanalyzer.dataanalyzer.models.*
import kotlinx.datetime.*

interface AnalysisDAO {
    suspend fun allArticles(): List<Article>
    suspend fun allTopics(): List<Topic>
    suspend fun mostRecentDate(): Instant?
}