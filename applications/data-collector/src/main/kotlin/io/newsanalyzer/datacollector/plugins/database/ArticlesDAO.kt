package io.newsanalyzer.datacollector.plugins.database

import io.newsanalyzer.datacollector.models.*
import kotlinx.datetime.*

interface ArticlesDAO {
    suspend fun allArticles(): List<Article>
    suspend fun latestDateTime(): Instant?
    suspend fun updateArticles(): Boolean
}