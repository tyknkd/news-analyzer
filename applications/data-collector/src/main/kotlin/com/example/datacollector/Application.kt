package com.example.datacollector

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

import io.ktor.http.*
import java.io.File

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
    val client = HttpClient(Java)
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
    File("techIndustrySelectSources.json").writeText(response.bodyAsText())
    client.close()
}

