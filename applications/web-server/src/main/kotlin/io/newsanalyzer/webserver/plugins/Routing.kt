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

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            val articlesByTopic = analyzedDataGateway.allArticlesByTopic()
            call.respond(FreeMarkerContent("index.ftl", mapOf("articlesByTopic" to articlesByTopic)))
        }
        get("/topics") {
            val topics = analyzedDataGateway.allTopics()
            call.respond(FreeMarkerContent("topics.ftl", mapOf("topics" to topics)))
        }
        get("/topics/{topicId}/articles") {
            val topicId = call.parameters.getOrFail<Int>("topicId").toInt()
            val articlesOnTopic = analyzedDataGateway.articlesOnTopic(topicId)
            call.respond(FreeMarkerContent("articles.ftl", mapOf("articlesOnTopic" to articlesOnTopic)))
        }
        get("/about") {
            call.respond(FreeMarkerContent("about.ftl",null))
        }
        get("/api") {
            call.respond(status = HttpStatusCode.OK, entrypoint)
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
        staticResources("/styles", "styles")
    }
}
