package io.newsanalyzer.dataanalyzer.models

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
data class ArticleTopic (
    val id: Int,
    val topicId: Int
)