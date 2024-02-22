package io.newsanalyzer.datacollector.plugins

import io.newsanalyzer.datacollector.models.*
import kotlinx.datetime.*

interface ArticlesDAO {
    suspend fun allArticles(): List<Article>
    suspend fun mostRecentDate(): Instant?
}