package io.newsanalyzer.dataanalyzer.models

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
data class TermIndices (
    val topic: Int,
    val termIndices: List<Int>
)