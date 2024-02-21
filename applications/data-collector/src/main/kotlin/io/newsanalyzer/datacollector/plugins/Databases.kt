package io.newsanalyzer.datacollector.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )

    val dataGateway = DataGateway(database)

    runBlocking {
        launch {
            val collector = Collector()
            val remoteData = collector.collectData()
            dataGateway.addArticles(remoteData)
        }
    }

    routing {
        get("/articles") {
            call.respond(dataGateway.allArticles())
        }
    }
}
