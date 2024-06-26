package test.newsanalyzer.dataanalyzer

import io.newsanalyzer.dataanalyzer.plugins.*
import io.newsanalyzer.testsupport.*
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.awaitility.kotlin.await
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.junit.BeforeClass
import org.junit.AfterClass
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun testArticles() = testSuspend {
        testClient.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            val articles: List<Article> = body()
            assertEquals(TestDoubles.rawArticles.size, articles.size)
            for (article in articles) {
                assertTrue(article.topicId >= 0)
            }
        }
    }
    @Test
    fun testHealth() = testSuspend {
        testClient.get("health").apply {
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
        val articles: List<Article> = mqPublished.articles
        assertEquals(TestDoubles.rawArticles.size, articles.size)
        for (article in articles) {
            assertTrue(article.topicId >= 0)
        }
        assertTrue(mqPublished.topics.size >= 2)
    }
    @Test
    fun testTopics() = testSuspend {
        testClient.get("topics").apply {
            assertEquals(HttpStatusCode.OK, status)
            val topics: List<Topic> = body()
            assertTrue(topics.size >= 2)
        }
    }
    companion object {
        private val tables: List<Table> = listOf(RawArticles, AnalyzedArticles, Topics)
        private val database: Database = DatabaseTemplate(System.getenv("ANALYZER_TEST_DB"), emptyList()).database
        private lateinit var mqPublished: AnalyzedData
        private fun messageHandler(message: String): Boolean {
            mqPublished = Json.decodeFromString(message)
            return true
        }
        private val testApp = TestApplication {
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
                Messaging.updateCollectorMessenger(
                    exchangeName = "analyzer_app_test_collector_exchange",
                    queueName = "analyzer_app_test_collector_queue",
                    routingKey = "analyzer_app_test_collector_key"
                )
                Messaging.updateAnalyzerMessenger(
                    exchangeName = "analyzer_app_test_analyzer_exchange",
                    queueName = "analyzer_app_test_analyzer_queue",
                    routingKey = "analyzer_app_test_analyzer_key",
                    messageHandler = ::messageHandler
                )
                Messaging.collectorMessenger.listen()
                Messaging.analyzerMessenger.listen()
                RawDataGateway.addArticles(TestDoubles.rawArticles)
                await.atMost(TestSettings.mqTimeout).untilAsserted {
                    assertNotNull(mqPublished)
                }
            }
       }
        @AfterClass
        @JvmStatic
        fun teardown() {
            Messaging.collectorMessenger.delete()
            Messaging.analyzerMessenger.delete()
            transaction(database) {
                for (table in tables) {
                    SchemaUtils.drop(table)
                }
            }
            testApp.stop()
        }
    }
}