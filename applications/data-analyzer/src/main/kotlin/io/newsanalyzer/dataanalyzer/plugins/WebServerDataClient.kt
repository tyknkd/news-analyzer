package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.httpsupport.*

object WebServerDataClient {
    suspend fun postAnalyzedData(analyzedData: AnalyzedData, client: HttpClient = HttpClientTemplate().httpClient): Boolean {
        val apiHost = HostPaths().getWebServerPath()
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