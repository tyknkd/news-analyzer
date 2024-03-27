package io.newsanalyzer.httpsupport

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

open class HttpClientTemplate {
    val httpClient = HttpClient(engineFactory = Java) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                coerceInputValues = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }
}