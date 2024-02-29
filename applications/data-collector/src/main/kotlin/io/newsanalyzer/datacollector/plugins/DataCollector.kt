package io.newsanalyzer.datacollector.plugins

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.newsanalyzer.datacollector.models.RemoteData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Paths

class DataCollector {
    suspend fun collectData(): RemoteData {
        val newsApiKey = if (System.getenv("OS_ENV") == "container") {
            withContext(Dispatchers.IO) {
                Files.readAllBytes(Paths.get(System.getenv("NEWS_API_KEY_FILE")))
            }.toString()
        } else {
            System.getenv("NEWS_API_KEY")
        }

        val apiKeyErrorMessage = "NEWS_API_KEY environment variable is invalid or not set. Check your key, or obtain a free key from https://newsapi.org"
        if ((newsApiKey == null) or (newsApiKey == "yournewsapikeygoeshere")) {
            throw RuntimeException(apiKeyErrorMessage)
        }
        // Example API request:
        // GET https://newsapi.org/v2/everything?q=%22tech%20industry%22&sources=ars-technica,associated-press&sortBy=publishedAt&apiKey=NEWS_API_KEY
        // Responds with summaries of up to 100 matching articles/page from past month up to yesterday when using free developer account
        val apiHost = "newsapi.org"
        val path = "v2/everything"
        val topic = "\"tech industry\""
        val sourceList = listOf(
            "ars-technica",
            "associated-press",
            "bbc-news",
            "bloomberg",
            "business-insider",
            "engadget",
            "fortune",
            "hacker-news",
            "new-scientist",
            "newsweek",
            "next-big-future",
            "recode",
            "reuters",
            "techcrunch",
            "techradar",
            "the-next-web",
            "the-verge",
            "the-wall-street-journal",
            "the-washington-post",
            "wired"
        )
        val sources = sourceList.joinToString(",")
        val sortBy = "publishedAt"
        val client = HttpClient(engineFactory = Java) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                })
            }
        }
        val response: HttpResponse = client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = apiHost
                path(path)
                parameters.append("q", topic)
                parameters.append("sources", sources)
                parameters.append("sortBy", sortBy)
                parameters.append("apiKey", newsApiKey)
            }
        }
        client.close()
        if(response.status != HttpStatusCode.OK){
            throw RuntimeException(apiKeyErrorMessage)
        }
        val remoteData: RemoteData = response.body()
        return remoteData
    }
}