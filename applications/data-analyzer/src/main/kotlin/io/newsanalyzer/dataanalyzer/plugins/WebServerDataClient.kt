package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.newsanalyzer.dataanalyzer.models.*
import kotlinx.serialization.json.Json

object WebServerDataClient {
    suspend fun postAnalyzedData(analyzedData: AnalyzedData): Boolean {
        val port = System.getenv("WEBSERVER_PORT")
        val apiHost = if (System.getenv("OS_ENV") == "container") {
            "web-server:$port"
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
        val response: HttpResponse = client.post("api/update") {
            contentType(ContentType.Application.Json)
            setBody(analyzedData)
        }

        client.close()

        return (response.bodyAsText() == "Updated")
    }
}