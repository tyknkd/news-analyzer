package io.newsanalyzer.dataanalyzer.models

import kotlinx.serialization.Serializable

@Serializable
data class Topic (
    val topicId: Int,
    val terms: String
)