package io.newsanalyzer.datacollector.plugins

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.newsanalyzer.datacollector.models.*
import kotlinx.serialization.json.Json

object AnalyzerDataClient {
    suspend fun postArticles(articles: List<Article>): Boolean {
        val port = System.getenv("ANALYZER_PORT")
        val apiHost = if (System.getenv("OS_ENV") == "container") {
            "data-analyzer:$port"
        } else {
            "localhost:$port"
        }
        val client = HttpClient(engineFactory = Java) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTP
                    host = apiHost
                }
            }
        }
        val response: HttpResponse = client.post("articles") {
            contentType(ContentType.Application.Json)
            setBody(articles)
        }

        client.close()

        return if (response.status == HttpStatusCode.OK) {
            true
        } else {
            false
        }
    }
}