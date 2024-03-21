package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.newsanalyzer.dataanalyzer.plugins.database.AnalyzedDataGateway
import io.newsanalyzer.dataanalyzer.models.Article
import io.newsanalyzer.dataanalyzer.plugins.database.RawDataGateway

fun Application.configureRouting() {
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
            val topics = AnalyzedDataGateway.allTopics()
            call.respond(status = HttpStatusCode.OK, topics)
        }
        get("/articles") {
            val articles = AnalyzedDataGateway.allArticles()
            call.respond(status = HttpStatusCode.OK, articles)
        }
        post("/articles") {
            val articles = call.receive<List<Article>>()
            RawDataGateway.addArticles(articles)
            call.respondText("Articles received", status = HttpStatusCode.OK)
        }
        get("/health") {
            call.respondText(text = "OK", status = HttpStatusCode.OK)
        }
    }
}
