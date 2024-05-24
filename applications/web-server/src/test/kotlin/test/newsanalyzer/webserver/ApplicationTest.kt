package test.newsanalyzer.webserver

import io.ktor.client.call.*
import io.newsanalyzer.webserver.plugins.*
import io.newsanalyzer.webserver.models.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.datasupport.models.*
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
import kotlin.test.assertContains
import kotlin.test.assertEquals

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ApplicationTest {
    @Test
    fun testAbout() = testSuspend {
        testClient.get("/about").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText,"Tech Industry News Analyzer")
            assertContains(bodyAsText,"tech industry news articles")
            assertContains(bodyAsText,"href=\"/\"")
        }
    }
    @Test
    fun testApiArticles() = testSuspend {
        testClient.get("/api/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            val articles: List<Article> = body()
            assertEquals(TestDoubles.analyzedArticles, articles)
        }
    }
    @Test
    fun testApiArticlesByTopic() = testSuspend {
        testClient.get("/api/topics/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            val articlesByTopic: List<ArticlesByTopic> = body()
            assertEquals(TestDoubles.articlesByTopic, articlesByTopic)
        }
    }
    @Test
    fun testApiArticlesOnTopic() = testSuspend {
        testClient.get("/api/topics/0/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            val articlesOnTopic: ArticlesByTopic = body()
            assertEquals(TestDoubles.articlesOnTopic, articlesOnTopic)
        }
    }
    @Test
    fun testApiEntrypoint() = testSuspend {
        testClient.get("/api").apply {
            assertEquals(HttpStatusCode.OK, status)
            val endpoint: Endpoint = body()
            assertEquals(entrypoint, endpoint)
        }
    }
    @Test
    fun testApiTopics() = testSuspend {
        testClient.get("/api/topics").apply {
            assertEquals(HttpStatusCode.OK, status)
            val topics: List<Topic> = body()
            assertEquals(TestDoubles.topics, topics)
        }
    }
    @Test
    fun testArticlesOnTopic() = testSuspend {
        testClient.get("/topics/1/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText,"Tech Industry News Analyzer")
            assertContains(bodyAsText,"Topic Group 1")
            assertContains(bodyAsText,"href=\"/\"")
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
    fun testRoot() = testSuspend {
        testClient.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText,"Tech Industry News Analyzer")
            assertContains(bodyAsText,"Topic Group 0")
            assertContains(bodyAsText,"Topic Group 1")
            assertContains(bodyAsText,"href=\"/topics/1/articles\"")
            for (article in TestDoubles.analyzedArticles) {
                assertContains(bodyAsText,article.title)
                assertContains(bodyAsText,article.url)
            }
            assertContains(bodyAsText,"href=\"${TestDoubles.analyzedArticles[0].url}\"")
        }
    }
    @Test
    fun testTopics() = testSuspend {
        testClient.get("/topics").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText,"Tech Industry News Analyzer")
            assertContains(bodyAsText,"Topic Group 0")
            assertContains(bodyAsText,"Topic Group 1")
            assertContains(bodyAsText,"href=\"/\"")
            assertContains(bodyAsText,"href=\"/topics/1/articles\"")
        }
    }
    @Test
    fun testTopic0Articles() = testSuspend {
        testClient.get("/topics/0/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText,"Tech Industry News Analyzer")
            assertContains(bodyAsText,"Topic Group 0")
            assertContains(bodyAsText,"href=\"/\"")
            assertContains(bodyAsText,"href=\"https://www.example.com/")
        }
    }
    companion object {
        private val tables: List<Table> = listOf(AnalyzedArticles, Topics)
        private val database: Database = DatabaseTemplate(System.getenv("WEBSERVER_TEST_DB"), emptyList()).database
        private val testApp = TestApplication {
            application {
                configureMonitoring()
                configureSerialization()
                configureTemplating()
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
                WebDataGateway.updateAll(TestDoubles.analyzedArticles,TestDoubles.topics)
            }
        }
        @AfterClass
        @JvmStatic
        fun teardown() {
            transaction(database) {
                for (table in tables) {
                    SchemaUtils.drop(table)
                }
            }
            testApp.stop()
        }
    }
}
