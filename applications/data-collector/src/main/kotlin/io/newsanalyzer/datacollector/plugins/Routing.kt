package io.newsanalyzer.datacollector.plugins

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.newsanalyzer.httpsupport.HttpClientTemplate
import kotlinx.coroutines.runBlocking

fun Application.configureRouting(httpClient: HttpClient = HttpClientTemplate().httpClient) {
    CollectorDataGateway.updateClient(httpClient)
    runBlocking { CollectorDataGateway.updateArticles(httpClient) }
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respondRedirect("/articles")
        }
        get("/health") {
            call.respondText(text = "OK", status = HttpStatusCode.OK)
        }
        get("/articles") {
            val articles = CollectorDataGateway.allArticles()
            if (articles.isEmpty() ) {
                call.respond(status = HttpStatusCode.Unauthorized, "NEWS_API_KEY environment variable is invalid or not set. Check your key, or obtain a free key from https://newsapi.org")
            } else {
                call.respond(status = HttpStatusCode.OK, articles)
            }
        }
        get("/update") {
            if (CollectorDataGateway.updateArticles(httpClient)) {
                call.respondText(text = "Updated", status = HttpStatusCode.OK)
            } else {
                call.respondText(text = "No update", status = HttpStatusCode.OK)
            }
        }
    }
}
