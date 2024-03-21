package io.newsanalyzer.dataanalyzer.plugins

import io.newsanalyzer.dataanalyzer.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object CollectedDataClient {
    suspend fun getCollectedData(): List<Article> {
        val port = System.getenv("COLLECTOR_PORT")
        val apiHost = if (System.getenv("OS_ENV") == "container") {
            "data-collector:$port"
        } else {
            "localhost:$port"
        }
        val path = "articles"
        val client = HttpClient(engineFactory = Java) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
        val response: HttpResponse = client.get {
            url {
                protocol = URLProtocol.HTTP
                host = apiHost
                path(path)
            }
        }
        client.close()
        val articleList: List<Article> = response.body()
        return articleList
    }
}