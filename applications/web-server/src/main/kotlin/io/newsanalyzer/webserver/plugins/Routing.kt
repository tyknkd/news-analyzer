package io.newsanalyzer.webserver.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.newsanalyzer.webserver.models.exampleArticles

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("articles" to exampleArticles)))
        }
        get("/articles/{id}") {
            val id = call.parameters.getOrFail<Int>("id").toInt()
            call.respond(FreeMarkerContent("article.ftl", mapOf("article" to exampleArticles.find { it.id == id })))
        }
        get("/api") {
            call.respondText(text = "News Analyzer API", status = HttpStatusCode.OK)
        }
        get("/api/topics") {
            val topics = analyzedDataGateway.allTopics()
            call.respond(status = HttpStatusCode.OK, topics)
        }
        get("/api/articles") {
            val articles = analyzedDataGateway.allArticles()
            call.respond(status = HttpStatusCode.OK, articles)
        }
        get("/api/topics/articles") {
            val articlesByTopic = analyzedDataGateway.allArticlesByTopic()
            call.respond(status = HttpStatusCode.OK, articlesByTopic)
        }
        get("/health") {
            call.respondText(text = "OK", status = HttpStatusCode.OK)
        }
        staticResources("/static", "static")
        staticResources("/styles", "styles")
    }
}
