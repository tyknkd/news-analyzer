package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Results(val status: String = "",
                   val totalResults: Int,
                   val articles: List<Article>)