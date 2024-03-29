package test.newsanalyzer.datacollector

import io.ktor.client.call.*
import io.newsanalyzer.datacollector.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.httpsupport.HostPaths
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ApplicationTest {
    private val apiKeyErrorMessage = "NEWS_API_KEY environment variable is invalid or not set. Check your key, or obtain a free key from https://newsapi.org"

    @Test
    fun testApiKey() {
        val newsApiKey = System.getenv("NEWS_API_KEY")
        assertNotNull(newsApiKey, apiKeyErrorMessage)
        assertNotEquals(newsApiKey,"yournewsapikeygoeshere", apiKeyErrorMessage)
    }
    @Test
    fun testArticles() = testApplication {
        lateinit var database: Database
        val tables: List<Table> = listOf(RawArticles)
        externalServices {
            hosts("https://newsapi.org") {
                install(ContentNegotiation) { json() }
                routing {
                    get("v2/everything{params}") {
                        call.respond(status = HttpStatusCode.OK, TestDoubles.remoteData)
                    }
                }
            }
            val apiHost = HostPaths().getAnalyzerPath()
            hosts("http://${apiHost}") {
                install(ContentNegotiation) { json() }
                routing {
                    post("articles") {
                        call.respondText("Updated", status = HttpStatusCode.OK)
                    }
                }
            }
        }
        val testClient = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { json() }
        }
        application {
            configureSerialization()
            database = configureDatabases("COLLECTOR_TEST_DB", tables)
            configureRouting(testClient)
        }
        val response = testClient.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        val articles: List<Article> = response.body()
        assertEquals(TestDoubles.rawArticles, articles)

        transaction(database) {
            for (table in tables) {
                    SchemaUtils.drop(table)
            }
        }
    }
    @Test
    fun testHealth() = testApplication {
        lateinit var database: Database
        val tables: List<Table> = listOf(RawArticles)
        externalServices {
            hosts("https://newsapi.org") {
                install(ContentNegotiation) { json() }
                routing {
                    get("v2/everything{params}") {
                        call.respond(status = HttpStatusCode.OK, TestDoubles.remoteData)
                    }
                }
            }
            val apiHost = HostPaths().getAnalyzerPath()
            hosts("http://${apiHost}") {
                install(ContentNegotiation) { json() }
                routing {
                    post("articles") {
                        call.respondText("Updated", status = HttpStatusCode.OK)
                    }
                }
            }
        }
        val testClient = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { json() }
        }
        application {
            configureSerialization()
            database = configureDatabases("COLLECTOR_TEST_DB", tables)
            configureRouting(testClient)
        }
        testClient.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("OK", bodyAsText())
        }
        transaction(database) {
            for (table in tables) {
                SchemaUtils.drop(table)
            }
        }
    }
}