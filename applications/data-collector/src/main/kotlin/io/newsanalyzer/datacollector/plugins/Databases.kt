package io.newsanalyzer.datacollector.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )

    val dataGateway = DataGateway(database)

    routing {
    }
}
