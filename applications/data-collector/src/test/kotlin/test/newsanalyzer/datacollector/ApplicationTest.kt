package test.newsanalyzer.datacollector

import io.newsanalyzer.datacollector.plugins.*
import io.newsanalyzer.testsupport.*
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
import kotlinx.serialization.json.Json
import org.awaitility.kotlin.await
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {
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
    @Test
    fun testMetrics() = testSuspend {
        testClient.get("/metrics").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText, "jvm_memory_used_bytes")
            assertContains(bodyAsText,"ktor_http_server_requests_seconds")
        }
    }
    @Test
    fun testMqPublished() = testSuspend {
        await.atMost(TestSettings.mqTimeout).untilAsserted {
            assertEquals(TestDoubles.rawArticles, mqPublished)
        }
    }
    companion object {
        private val tables: List<Table> = listOf(RawArticles)
        private val database: Database = DatabaseTemplate(System.getenv("COLLECTOR_TEST_DB"), emptyList()).database
        private lateinit var mqPublished: List<Article>
        private fun messageHandler(message: String): Boolean {
            mqPublished = Json.decodeFromString(message)
            return true
        }
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
                configureMonitoring()
                configureSerialization()
                configureRouting()
            }
        }
        private val testClient = testApp.createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { json() }
        }

        @BeforeClass
        @JvmStatic
        fun setup() {
            testSuspend {
                transaction(database) {
                    for (table in tables) {
                        SchemaUtils.create(table)
                    }
                }
                CollectorDataGateway.updateClient(testClient)
                Messaging.updateMessenger(
                    exchangeName = "collector_app_test_exchange",
                    queueName = "collector_app_test_queue",
                    routingKey = "collector_app_test_key",
                    messageHandler = ::messageHandler
                )
                Messaging.collectorMessenger.listen()
                CollectorDataGateway.updateArticles()
            }
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            Messaging.collectorMessenger.delete()
            transaction(database) {
                for (table in tables) {
                    SchemaUtils.drop(table)
                }
            }
            testApp.stop()
        }
    }
}