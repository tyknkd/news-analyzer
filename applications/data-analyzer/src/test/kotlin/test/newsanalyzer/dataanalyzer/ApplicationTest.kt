package test.newsanalyzer.dataanalyzer

import io.newsanalyzer.dataanalyzer.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.junit.BeforeClass
import org.junit.AfterClass
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
    fun testTopics() = testSuspend {
        testClient.get("topics").apply {
            assertEquals(HttpStatusCode.OK, status)
            val topics: List<Topic> = body()
            assertTrue(topics.size >= 2)
        }
    }
//    @Test
//    fun testUpdate() = testSuspend {
//        testClient.post("articles") {
//            contentType(ContentType.Application.Json)
//            setBody(TestDoubles.updatedRawArticles)
//        }.apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("Updated", bodyAsText())
//        }
//
//        testClient.get("articles").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            val articles: List<Article> = body()
//            assertEquals(TestDoubles.updatedRawArticles.size, articles.size)
//            for (article in articles) {
//                assertTrue(article.topicId >= 0)
//            }
//        }
//    }
    companion object {
        private val tables: List<Table> = listOf(RawArticles, AnalyzedArticles, Topics)
        private val database: Database = DatabaseTemplate("ANALYZER_TEST_DB", emptyList()).database
        private val testApp = TestApplication {
            application {
                configureSerialization()
                configureRouting()
            }
        }
        private val testClient = testApp.createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { json() }
        }
        @BeforeClass
        @JvmStatic
        fun setup(): Unit {
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
                routingKey = "analyzer_app_test_analyzer_key"
            )
            testSuspend { RawDataGateway.addArticles(TestDoubles.rawArticles) }
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