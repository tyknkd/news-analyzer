package io.newsanalyzer.webserver.plugins

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object CollectorDataClient {
    suspend fun requestUpdate(): Boolean {
        val port = System.getenv("COLLECTOR_PORT")
        val apiHost = if (System.getenv("OS_ENV") == "container") {
            "data-collector:$port"
        } else {
            "localhost:$port"
        }
        val path = "update"
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
        val result = response.bodyAsText()
        return (result == "Updated")
    }
}