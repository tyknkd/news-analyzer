package io.newsanalyzer.datacollector.models

import kotlinx.serialization.Serializable

@Serializable
data class RemoteArticle(
    val source: RemoteSource,
    val author: String = "",
    val title: String = "",
    val description: String = "",
    val url: String = "",
    val urlToImage: String = "",
    val publishedAt: String = "",
    val content: String = ""
)