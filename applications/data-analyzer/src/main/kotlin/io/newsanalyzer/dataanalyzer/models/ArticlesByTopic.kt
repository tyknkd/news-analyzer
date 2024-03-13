package io.newsanalyzer.dataanalyzer.models

import kotlinx.serialization.Serializable

@Serializable
data class ArticlesByTopic (
    val topic: Topic,
    val articles: List<Article>
)