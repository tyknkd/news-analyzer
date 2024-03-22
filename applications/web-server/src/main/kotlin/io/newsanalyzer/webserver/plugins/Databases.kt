package io.newsanalyzer.webserver.plugins

import io.ktor.server.application.*
import io.newsanalyzer.webserver.plugins.database.WebDataGateway
import io.newsanalyzer.webserver.plugins.database.WebDatabase

fun Application.configureDatabases() {
    WebDatabase.init()
}
