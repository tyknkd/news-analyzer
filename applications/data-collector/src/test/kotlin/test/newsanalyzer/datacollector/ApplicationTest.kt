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
import io.ktor.test.dispatcher.*
import io.newsanalyzer.datasupport.DatabaseTemplate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class ApplicationTest {
    private val tables: List<Table> = listOf(RawArticles)
    private val database: Database = DatabaseTemplate("COLLECTOR_TEST_DB", emptyList()).database
    private val testApp = TestApplication {
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
        application {
            configureSerialization()
            configureRouting()
        }
    }
    private val testClient = testApp.createClient {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { json() }
    }
    @BeforeTest
    fun setup() {
        transaction(database) {
            for (table in tables) {
                SchemaUtils.create(table)
            }
        }
        CollectorDataGateway.updateClient(testClient)
        testSuspend { CollectorDataGateway.updateArticles() }
    }
    @AfterTest
    fun teardown() {
        transaction(database) {
            for (table in tables) {
                SchemaUtils.drop(table)
            }
        }
    }
    @Test
    fun testArticles() = testSuspend {
        val response = testClient.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        val articles: List<Article> = response.body()
        assertEquals(TestDoubles.rawArticles, articles)
    }
    @Test
    fun testHealth() = testSuspend {
        testClient.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("OK", bodyAsText())
        }
    }
}