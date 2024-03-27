package io.newsanalyzer.datasupport.models

import kotlinx.serialization.Serializable

@Serializable
data class RemoteData(
    val status: String,
    val totalResults: Int,
    val articles: List<RemoteArticle>
)

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

@Serializable
data class RemoteSource(val id: String?, val name: String = "")