package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.newsanalyzer.datasupport.models.Article
import io.newsanalyzer.httpsupport.HttpClientTemplate

fun Application.configureRouting(httpClient: HttpClient = HttpClientTemplate().httpClient) {
    val rawDataGateway = RawDataGateway(httpClient)
    val analyzedDataGateway = AnalyzedDataGateway(httpClient)
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
        get("/topics") {
            val topics = analyzedDataGateway.allTopics()
            call.respond(status = HttpStatusCode.OK, topics)
        }
        get("/articles") {
            val articles = analyzedDataGateway.allArticles()
            call.respond(status = HttpStatusCode.OK, articles)
        }
        post("/articles") {
            val articles = call.receive<List<Article>>()
            if(rawDataGateway.addArticles(articles)) {
                call.respondText("Updated", status = HttpStatusCode.OK)
            } else {
                call.respondText("Not updated", status = HttpStatusCode.OK)
            }
        }
        get("/reanalyze") {
            if(analyzedDataGateway.updateAll(httpClient)) {
                call.respondText("Updated", status = HttpStatusCode.OK)
            } else {
                call.respondText("Not updated", status = HttpStatusCode.OK)
            }
        }
        get("/health") {
            call.respondText(text = "OK", status = HttpStatusCode.OK)
        }
    }
}
