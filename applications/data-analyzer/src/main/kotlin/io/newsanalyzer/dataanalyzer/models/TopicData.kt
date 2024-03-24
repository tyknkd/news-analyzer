package io.newsanalyzer.dataanalyzer.models

import io.newsanalyzer.datasupport.models.Article
import org.jetbrains.kotlinx.dataframe.DataFrame

data class TopicData (
    val articles: DataFrame<Article>,
    val terms: List<String>,
    val termIndices: List<TermIndices>,
    val articleTopics: List<ArticleTopic>
)

data class TermIndices (
    val topic: Int,
    val termIndices: List<Int>
)

data class ArticleTopic (
    val id: Int,
    val topicId: Int
)

data class ArticleText (
    val id: Int,
    val text: String
)