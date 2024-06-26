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
        get("/reanalyze") {
            if(AnalyzedDataGateway.updateAll()) {
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
