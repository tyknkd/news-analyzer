package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.newsanalyzer.datasupport.models.*
import kotlinx.serialization.json.Json

object WebServerDataClient {
    suspend fun postAnalyzedData(analyzedData: AnalyzedData, client: HttpClient = AnalyzerClient.httpClient): Boolean {
        val port = System.getenv("WEBSERVER_PORT")
        val apiHost = if (System.getenv("OS_ENV") == "container") {
            "web-server:$port"
        } else {
            "localhost:$port"
        }
        val path = "api/update"
        val response: HttpResponse = client.post {
            url {
                protocol = URLProtocol.HTTP
                host = apiHost
                path(path)
            }
            contentType(ContentType.Application.Json)
            setBody(analyzedData)
        }
        return (response.bodyAsText() == "Updated")
    }
}