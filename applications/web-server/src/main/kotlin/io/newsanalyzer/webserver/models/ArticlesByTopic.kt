package io.newsanalyzer.webserver.models

import kotlinx.serialization.Serializable

@Serializable
data class ArticlesByTopic (
    val topic: Topic,
    val articles: List<Article>
)