package io.newsanalyzer.datacollector.plugins

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.newsanalyzer.datasupport.models.RemoteArticle
import io.newsanalyzer.datasupport.models.RemoteData
import io.newsanalyzer.httpsupport.HttpClientTemplate
import kotlinx.datetime.*
import org.jetbrains.kotlinx.dataframe.api.*

object DataCollector {
    suspend fun collectData(fromInstant: Instant?=null, client: HttpClient = HttpClientTemplate().httpClient): List<RemoteArticle>? {
        val sourceList = listOf("ars-technica","associated-press","bbc-news","bloomberg","business-insider","engadget",
            "fortune","hacker-news","new-scientist","newsweek","next-big-future","recode","reuters","techcrunch",
            "techradar","the-next-web","the-verge","the-wall-street-journal","the-washington-post","wired")
        return getNewsApiData("tech industry", sourceList, getNewsApiKey(), fromInstant, client)
    }

    private fun getNewsApiKey(): String? {
        return System.getenv("NEWS_API_KEY")
    }

    suspend fun getNewsApiData(topic: String,
                               sourceList: List<String>,
                               newsApiKey: String?,
                               fromInstant: Instant? = null,
                               client: HttpClient = HttpClientTemplate().httpClient): List<RemoteArticle>? {
        val apiKeyErrorMessage = "NEWS_API_KEY environment variable is invalid or not set. Check your key, or obtain a free key from https://newsapi.org"
        if (newsApiKey == null) {
            throw RuntimeException(apiKeyErrorMessage)
        }
        // Example API request:
        // GET https://newsapi.org/v2/everything?q=%22tech%20industry%22&sources=ars-technica%2Cassociated-press&sortBy=publishedAt&apiKey=NEWS_API_KEY
        // Responds with summaries of up to 100 matching articles/page from past month up to yesterday when using free developer account
        val apiHost = "newsapi.org"
        val path = "v2/everything"
        val topicQuoted = "\"${topic}\""
        val sources = sourceList.joinToString(",")
        val sortBy = "publishedAt"

        val response: HttpResponse = client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = apiHost
                path(path)
                parameters.append("q", topicQuoted)
                parameters.append("sources", sources)
                parameters.append("sortBy", sortBy)
                if (fromInstant != null) { parameters.append("from", fromInstant.toString()) }
                parameters.append("apiKey", newsApiKey)
            }
        }

        if(response.status != HttpStatusCode.OK){
            throw RuntimeException(apiKeyErrorMessage)
        }
        val remoteData: RemoteData = response.body()
        return if (remoteData.totalResults > 0) { cleanData(remoteData) } else { null }
    }

    suspend fun cleanData(remoteData: RemoteData): List<RemoteArticle> {
        val articlesDf = remoteData.articles.reversed().toDataFrame()
        val filteredArticlesDf = articlesDf.drop { it["title"] == "[Removed]" || it["title"] == "" }
        return filteredArticlesDf.toList()
    }
}