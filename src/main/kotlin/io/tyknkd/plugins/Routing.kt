package io.tyknkd.plugins

import io.tyknkd.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("articles" to articles)))
        }
        get("/articles/{id}") {
            val id = call.parameters.getOrFail<Int>("id").toInt()
            call.respond(FreeMarkerContent("article.ftl", mapOf("article" to articles.find { it.id == id })))
        }
        get("/api") {
            call.respondText(text = "News Analyzer API", status = HttpStatusCode.OK)
        }
        get("/health") {
            call.respondText(text = "OK", status = HttpStatusCode.OK)
        }
        staticResources("/static", "static")
        staticResources("/styles", "styles")
    }
}
