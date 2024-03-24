package io.newsanalyzer.datacollector.plugins.database

import io.newsanalyzer.datasupport.models.*
import kotlinx.datetime.*

interface CollectorDAO {
    suspend fun allArticles(): List<Article>
    suspend fun articlesAfter(instant: Instant?): List<Article>
    suspend fun latestDateTime(): Instant?
    suspend fun updateArticles(): Boolean
}