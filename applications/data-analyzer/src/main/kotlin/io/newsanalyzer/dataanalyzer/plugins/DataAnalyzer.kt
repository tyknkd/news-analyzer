package io.newsanalyzer.dataanalyzer.plugins

import io.newsanalyzer.dataanalyzer.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
// import org.jetbrains.kotlinx.spark.api.*

class DataAnalyzer {
    suspend fun getData(): List<Article> {
        val port = System.getenv("COLLECTOR_PORT")
        lateinit var apiHost: String
        if (System.getenv("OS_ENV") == "container") {
            apiHost = "data-collector:$port"
        } else {
            apiHost = "localhost:$port"
        }
        val path = "articles"
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
                protocol = URLProtocol.HTTP
                host = apiHost
                path(path)
            }
        }
        client.close()
        val articles: List<Article> = response.body()
        return articles
    }
    // TODO: Convert articles list to Spark dataframe
    // TODO: Process text in each article
        // Combine title, description, content fields
        // Tokenize
        // Remove stop words, HTML tags, numbers
    // TODO: Apply LDA model to label each article
    // TODO: Return labelled article data
}

val dataAnalyzer = DataAnalyzer()