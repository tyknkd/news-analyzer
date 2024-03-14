package io.newsanalyzer.webserver.models

import kotlinx.serialization.Serializable

@Serializable
data class Topic (
    val topicId: Int,
    val terms: String
)