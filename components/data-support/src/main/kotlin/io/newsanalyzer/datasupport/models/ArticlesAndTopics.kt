package io.newsanalyzer.datasupport.models

import kotlinx.serialization.Serializable

@Serializable
data class AnalyzedData(
    val articles: List<Article>,
    val topics: List<Topic>
)

@Serializable
data class ArticlesByTopic (
    val topic: Topic,
    val articles: List<Article>
)