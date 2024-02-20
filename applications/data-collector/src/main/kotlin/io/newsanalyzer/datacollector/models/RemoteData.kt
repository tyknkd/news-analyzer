package io.newsanalyzer.datacollector.models

import kotlinx.serialization.Serializable

@Serializable
data class RemoteData(val status: String,
                      val totalResults: Int,
                      val articles: List<RemoteArticle>)