package io.newsanalyzer.dataanalyzer.models

import kotlinx.serialization.Serializable

@Serializable
data class AnalyzedData(
    val articles: List<Article>,
    val topics: List<Topic>
)