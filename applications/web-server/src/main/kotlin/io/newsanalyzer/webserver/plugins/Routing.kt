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
import io.newsanalyzer.webserver.models.entrypoint
import io.newsanalyzer.webserver.plugins.database.WebDataGateway

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            val articlesByTopic = WebDataGateway.allArticlesByTopic()
            call.respond(FreeMarkerContent("index.ftl", mapOf("articlesByTopic" to articlesByTopic)))
        }
        get("/topics") {
            val topics = WebDataGateway.allTopics()
            call.respond(FreeMarkerContent("topics.ftl", mapOf("topics" to topics)))
        }
        get("/topics/{topicId}/articles") {
            val topicId = call.parameters.getOrFail<Int>("topicId").toInt()
            val articlesOnTopic = WebDataGateway.articlesOnTopic(topicId)
            call.respond(FreeMarkerContent("articles.ftl", mapOf("articlesOnTopic" to articlesOnTopic)))
        }
        get("/about") {
            call.respond(FreeMarkerContent("about.ftl",null))
        }
        get("/api") {
            call.respond(status = HttpStatusCode.OK, entrypoint)
        }
        get("/api/topics") {
            val topics = WebDataGateway.allTopics()
            call.respond(status = HttpStatusCode.OK, topics)
        }
        get("/api/articles") {
            val articles = WebDataGateway.allArticles()
            call.respond(status = HttpStatusCode.OK, articles)
        }
        get("/api/topics/articles") {
            val articlesByTopic = WebDataGateway.allArticlesByTopic()
            call.respond(status = HttpStatusCode.OK, articlesByTopic)
        }
        get("api/topics/{topicId}/articles") {
            val topicId = call.parameters.getOrFail<Int>("topicId").toInt()
            val articlesOnTopic = WebDataGateway.articlesOnTopic(topicId)
            call.respond(status = HttpStatusCode.OK, articlesOnTopic)
        }
        get("/api/update") {
            val result = CollectorDataClient.requestUpdate()
            call.respondText(text = result.toString(), status = HttpStatusCode.OK)
        }
        get("/health") {
            call.respondText(text = "OK", status = HttpStatusCode.OK)
        }
        staticResources("/styles", "styles")
    }
}
