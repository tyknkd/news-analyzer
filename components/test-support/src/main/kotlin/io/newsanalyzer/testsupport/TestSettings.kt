package io.newsanalyzer.testsupport

import java.time.Duration.ofSeconds

object TestSettings {
    val mqMinLatency = 5000L
    val mqTimeout = ofSeconds(5)
}