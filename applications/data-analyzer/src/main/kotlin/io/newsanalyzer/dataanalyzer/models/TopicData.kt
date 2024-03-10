package io.newsanalyzer.dataanalyzer.models

import org.jetbrains.kotlinx.dataframe.DataFrame

data class TopicData (
    val articles: DataFrame<Article>,
    val terms: List<String>,
    val termIndices: List<TermIndices>,
    val articleTopics: List<ArticleTopic>
)