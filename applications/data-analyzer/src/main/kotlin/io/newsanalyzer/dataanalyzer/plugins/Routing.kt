package io.newsanalyzer.dataanalyzer.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respondRedirect("/articles", permanent = true)
        }
        get("/topics") {
            val topics = analysisGateway.allTopics()
            call.respond(status = HttpStatusCode.OK, topics)
        }
        get("/articles") {
            val articles = analysisGateway.allArticles()
            call.respond(status = HttpStatusCode.OK, articles)
        }
        get("/health") {
            call.respondText(text = "OK", status = HttpStatusCode.OK)
        }
    }
}
