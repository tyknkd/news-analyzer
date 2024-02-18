package com.example.datacollector

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

import java.io.File

@Serializable
data class Source(val id: String = "", val name: String = "")
@Serializable
data class Article(val source: Source,
                   val author: String = "",
                   val title: String = "",
                   val description: String = "",
                   val url: String = "",
                   val urlToImage: String = "",
                   val publishedAt: String = "",
                   val content: String = "")
@Serializable
data class Articles(val status: String = "",
                    val totalResults: Int,
                    val articles: List<Article>)

suspend fun main() {
    val newsApiKey = System.getenv("NEWS_API_KEY")
        ?: throw RuntimeException("Obtain API key from https://newsapi.org and set as environment variable NEWS_API_KEY")
    // Example API request:
    // GET https://newsapi.org/v2/everything?q=%22tech%20industry%22&sources=ars-technica,associated-press&sortBy=publishedAt&apiKey=NEWS_API_KEY
    // Responds with summaries of up to 100 matching articles/page from past month up to yesterday when using free developer account
    val apiHost = "newsapi.org"
    val path = "v2/everything"
    val topic = "\"tech industry\""
    val sourceList = listOf("ars-technica","associated-press","bbc-news","bloomberg","business-insider",
        "engadget","fortune","hacker-news","new-scientist","newsweek","next-big-future","recode","reuters",
        "techcrunch","techradar","the-next-web","the-verge","the-wall-street-journal","the-washington-post","wired")
    val sources = sourceList.joinToString(",")
    val sortBy = "publishedAt"
    val client = HttpClient(Java) {
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
    val articles: Articles = response.body()
    println("Total results: ${articles.totalResults}")
    File("example.json").writeText(response.bodyAsText())
}

