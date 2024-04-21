package test.newsanalyzer.datacollector

import io.newsanalyzer.datacollector.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.ktor.client.call.*
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
        Messaging.updateMessenger(
            exchangeName = System.getenv("COLLECTOR_TEST_EXCHANGE"),
            queueName = System.getenv("COLLECTOR_QUEUE"),
            routingKey = System.getenv("COLLECTOR_ROUTING_KEY")
        )
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
        testClient.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            val articles: List<Article> = body()
            assertEquals(TestDoubles.rawArticles, articles)
        }
    }
    @Test
    fun testHealth() = testSuspend {
        testClient.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("OK", bodyAsText())
        }
    }
}