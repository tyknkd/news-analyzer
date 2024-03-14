package test.newsanalyzer.webserver

import io.newsanalyzer.webserver.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.junit.Test
import org.junit.BeforeClass
import org.junit.AfterClass
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testSuspend {
        testApp.client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
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
    fun testAPI() = testSuspend {
        testApp.client.get("/api").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("News Analyzer API", bodyAsText())
        }
    }
    @Test
    fun testStatic() = testSuspend {
        testApp.client.get("/static").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testTopics() = testSuspend {
        testApp.client.get("/api/topics").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "\"terms\":")
        }
    }
    @Test
    fun testArticles() = testSuspend {
        testApp.client.get("/api/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "\"publishedAt\":")
        }
    }

    @Test
    fun testArticlesByTopic() = testSuspend {
        testApp.client.get("/api/topics/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "\"topic\":")
            assertContains(bodyAsText(), "\"articles\":")
            assertContains(bodyAsText(), "\"terms\":")
            assertContains(bodyAsText(), "\"publishedAt\":")
        }
    }

    companion object {
        lateinit var testApp: TestApplication
        @JvmStatic
        @BeforeClass
        fun setup(): Unit {
            testApp = TestApplication {
                application {
                    configureMonitoring()
                    configureSerialization()
                    configureDatabases()
                    configureTemplating()
                    configureRouting()
                }
            }
        }

        @JvmStatic
        @AfterClass
        fun teardown() {
            testApp.stop()
        }
    }
}
