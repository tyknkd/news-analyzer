package io.newsanalyzer.datacollector.models

import kotlinx.serialization.*

@Serializable
data class RemoteSource(val id: String?, val name: String = "")