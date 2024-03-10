package io.newsanalyzer.dataanalyzer.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
//import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
//
//@DataSchema
@Serializable
data class AnalyzedArticle(
    val id: Int,
    val publisher: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: Instant,
    val content: String,
    val topicId: Int,
    val terms: List<String>
)