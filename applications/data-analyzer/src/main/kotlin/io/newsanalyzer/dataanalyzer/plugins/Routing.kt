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
            // TODO: Redirect to /topics
            call.respondRedirect("/topics/articles", permanent = true)
        }
        get("/topics") {
            // TODO: Response: List of most common topics in articles
        }
        get("/topics/articles") {
            // TODO: Response: Json: Article data with inferred topics
            val articles = dataAnalyzer.getData()
            call.respond(status = HttpStatusCode.OK, articles)
        }
        get("/health") {
            call.respondText(text = "OK", status = HttpStatusCode.OK)
        }
    }
}
