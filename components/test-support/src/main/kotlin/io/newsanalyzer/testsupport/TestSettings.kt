package io.newsanalyzer.testsupport

import java.time.Duration
import java.time.Duration.ofSeconds

object TestSettings {
    val mqTimeout: Duration = ofSeconds(10)
}