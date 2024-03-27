package io.newsanalyzer.datacollector.plugins

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.httpsupport.HttpClientTemplate

object AnalyzerDataClient {
    suspend fun postArticles(articles: List<Article>, client: HttpClient = HttpClientTemplate().httpClient): Boolean {
        val port = System.getenv("ANALYZER_PORT")
        val apiHost = if (System.getenv("OS_ENV") == "container") {
            "data-analyzer:$port"
        } else {
            "localhost:$port"
        }
        val path = "articles"
        val response: HttpResponse = client.post {
            url {
                protocol = URLProtocol.HTTP
                host = apiHost
                path(path)
            }
            contentType(ContentType.Application.Json)
            setBody(articles)
        }
        return (response.bodyAsText() == "Updated")
    }
}