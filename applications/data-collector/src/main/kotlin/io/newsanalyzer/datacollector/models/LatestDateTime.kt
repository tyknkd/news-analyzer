package io.newsanalyzer.datacollector.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class LatestDateTime(
    val latestDateTime: Instant?
)