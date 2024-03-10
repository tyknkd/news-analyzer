package io.newsanalyzer.dataanalyzer.models

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
data class ArticleText (
    val id: Int,
    val text: String
)