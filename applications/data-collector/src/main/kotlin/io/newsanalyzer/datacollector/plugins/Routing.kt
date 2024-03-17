package io.newsanalyzer.datacollector.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.newsanalyzer.datacollector.models.LatestDateTime
import io.newsanalyzer.datacollector.plugins.database.ArticlesGateway

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
        get("/health") {
            call.respondText(text = "OK", status = HttpStatusCode.OK)
        }
        get("/articles") {
            val articles = ArticlesGateway.allArticles()
            if (articles.isEmpty() ) {
                call.respond(status = HttpStatusCode.Unauthorized, "NEWS_API_KEY environment variable is invalid or not set. Check your key, or obtain a free key from https://newsapi.org")
            } else {
                call.respond(status = HttpStatusCode.OK, articles)
            }

        }
        get("/latestDateTime") {
            val latestDateTime = LatestDateTime(latestDateTime = ArticlesGateway.latestDateTime())
            call.respond(status = HttpStatusCode.OK, latestDateTime)
        }
    }
}
