package io.newsanalyzer.datacollector.plugins

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.newsanalyzer.datacollector.models.*
import kotlinx.serialization.json.Json
import kotlinx.datetime.*
import org.jetbrains.kotlinx.dataframe.api.*

object DataCollector {
    suspend fun collectData(fromInstant: Instant?=null): List<RemoteArticle>? {
        val newsApiKey = System.getenv("NEWS_API_KEY")
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
                if (fromInstant != null) { parameters.append("from", fromInstant.toString())}
                parameters.append("apiKey", newsApiKey)
            }
        }
        client.close()
        if(response.status != HttpStatusCode.OK){
            throw RuntimeException(apiKeyErrorMessage)
        }
        val remoteData: RemoteData = response.body()
        return if (remoteData.totalResults > 0) {
            cleanData(remoteData)
        } else {
            null
        }
    }
    private suspend fun cleanData(remoteData: RemoteData): List<RemoteArticle> {
        val articlesDf = remoteData.articles.reversed().toDataFrame()
        val filteredArticlesDf = articlesDf.drop { it["title"] == "[Removed]"}
        return filteredArticlesDf.toList()
    }
}