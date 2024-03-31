package test.newsanalyzer.dataanalyzer

import io.newsanalyzer.dataanalyzer.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.newsanalyzer.httpsupport.HostPaths
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
import org.junit.Test
import org.junit.BeforeClass
import org.junit.AfterClass
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ApplicationTest {
    companion object {
        private val tables: List<Table> = listOf(RawArticles, AnalyzedArticles, Topics)
        private val database: Database = DatabaseTemplate("ANALYZER_TEST_DB", emptyList()).database
        private val testApp = TestApplication {
            externalServices {
                val apiHost = HostPaths().getWebServerPath()
                hosts("http://${apiHost}") {
                    install(ContentNegotiation) { json() }
                    routing {
                        post("api/update") {
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
        @BeforeClass
        @JvmStatic
        fun setup(): Unit {
            transaction(database) {
                for (table in tables) {
                    SchemaUtils.create(table)
                }
            }
            RawDataGateway.updateClient(testClient)
            AnalyzedDataGateway.updateClient(testClient)
            testSuspend { RawDataGateway.addArticles(TestDoubles.rawArticles) }
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
    @Test
    fun testArticles() = testSuspend {
        val response = testClient.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        val articles: List<Article> = response.body()
        assertEquals(TestDoubles.rawArticles.size, articles.size)
        for (article in articles) {
            assertTrue(article.topicId >= 0)
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
        val response = testClient.get("topics")
        assertEquals(HttpStatusCode.OK, response.status)
        val topics: List<Topic> = response.body()
        assertTrue(topics.size >= 2)
    }
    @Test
    fun testUpdate() = testSuspend {
        val postResponse = testClient.post("articles") {
            contentType(ContentType.Application.Json)
            setBody(TestDoubles.updatedRawArticles)
        }
        assertEquals("Updated", postResponse.bodyAsText())
        assertEquals(HttpStatusCode.OK, postResponse.status)
        val getResponse = testClient.get("articles")
        assertEquals(HttpStatusCode.OK, getResponse.status)
        val articles: List<Article> = getResponse.body()
        assertEquals(TestDoubles.updatedRawArticles.size, articles.size)
        for (article in articles) {
            assertTrue(article.topicId >= 0)
        }
    }
}