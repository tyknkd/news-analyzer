package io.newsanalyzer.dataanalyzer.models

data class TermIndices (
    val topic: Int,
    val termIndices: List<Int>
)