package io.newsanalyzer.webserver.plugins

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.newsanalyzer.webserver.models.*
import kotlinx.serialization.json.Json

class AnalyzedDataClient {
    suspend fun getAnalyzedData(): Pair<List<Article>,List<Topic>> {
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
        val articlesResponse: HttpResponse = client.get("articles")
        val topicsResponse: HttpResponse = client.get("topics")

        client.close()

        val articlesList: List<Article> = articlesResponse.body()
        val topicsList: List<Topic> = topicsResponse.body()

        return Pair(articlesList,topicsList)
    }
}