package test.newsanalyzer.datacollector

import io.newsanalyzer.datacollector.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import io.newsanalyzer.datasupport.models.RawArticles
import io.newsanalyzer.httpsupport.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.junit.BeforeClass
import org.junit.AfterClass
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import kotlin.test.assertEquals
import kotlin.test.assertContains
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
    fun testArticles() = testSuspend {
        testApp.client.get("/articles").apply {
            assertEquals(HttpStatusCode.OK, status, apiKeyErrorMessage)
            assertContains(bodyAsText(), "\"publishedAt\":")
        }
    }
    @Test
    fun testHealth() = testSuspend {
        testApp.client.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("OK", bodyAsText())
        }
    }

    @Test
    fun testRoot() = testSuspend {
        testApp.client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status, apiKeyErrorMessage)
            assertContains(bodyAsText(), "\"publishedAt\":")
        }
    }
    @Test
    fun testUpdate() = testSuspend {
        testApp.client.get("/update").apply {
            assertEquals(HttpStatusCode.OK, status, apiKeyErrorMessage)
            assertEquals("No update",bodyAsText())
        }
    }


    companion object {
        private val testClient = HttpClientTemplate().httpClient
        lateinit var testApp: TestApplication
        private lateinit var database: Database
        private val tables: List<Table> = listOf(RawArticles)
        @JvmStatic
        @BeforeClass
        fun setup() {
            testApp = TestApplication {
                externalServices {
                    hosts("https://newsapi.org") {
                        install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) { json() }
                        routing {
                            get("v2/everything{params}") {
                                println("In mock newsapi")
                                call.respond(status = HttpStatusCode.OK, TestDoubles().remoteData)
                            }
                        }
                    }
                    val apiHost = HostPaths().getAnalyzerPath()
                    hosts("http://${apiHost}") {
                        install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) { json() }
                        routing {
                            post("articles") {
                                println("In mock analyzer")
                                call.respondText("Updated", status = HttpStatusCode.OK)
                            }
                        }
                    }
                }
                application {
                    configureSerialization()
                    database = configureDatabases("COLLECTOR_TEST_DB")
                    configureRouting(testClient)
                }
            }
        }

        @JvmStatic
        @AfterClass
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