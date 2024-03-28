package test.newsanalyzer.dataanalyzer

import io.newsanalyzer.dataanalyzer.plugins.*
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.httpsupport.*
import org.jetbrains.exposed.sql.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {
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
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "\"publishedAt\":")
        }
    }

    @Test
    fun testTopics() = testSuspend {
        testApp.client.get("/topics").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "\"terms\":")
        }
    }

    @Test
    fun testArticles() = testSuspend {
        testApp.client.get("/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "\"publishedAt\":")
        }
    }

    companion object {
        private val testClient = HttpClientTemplate().httpClient
        lateinit var testApp: TestApplication
        private lateinit var database: Database
        private val tables: List<Table> = listOf(RawArticles, AnalyzedArticles, Topics)
        @JvmStatic
        @BeforeClass
        fun setup() {
            testApp = TestApplication {
                application {
                    configureSerialization()
                    database = configureDatabases("ANALYZER_TEST_DB")
                    configureRouting()
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