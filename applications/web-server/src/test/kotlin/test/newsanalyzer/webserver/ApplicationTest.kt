package test.newsanalyzer.webserver

import io.ktor.client.call.*
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
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText,"Tech Industry News Analyzer")
            assertContains(bodyAsText,"Topic Group 0")
            assertContains(bodyAsText,"More on this topic")
            assertContains(bodyAsText,"\"/topics/1/articles\"")
        }
    }

    @Test
    fun testTopics() = testSuspend {
        testApp.client.get("/topics").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText,"Tech Industry News Analyzer")
            assertContains(bodyAsText,"Topic Group 0")
            assertContains(bodyAsText,"href=\"/\"")
            assertContains(bodyAsText,"\"/topics/1/articles\"")
        }
    }

    @Test
    fun testArticlesOnTopic() = testSuspend {
        testApp.client.get("/topics/1/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText,"Tech Industry News Analyzer")
            assertContains(bodyAsText,"Topic Group 1")
            assertContains(bodyAsText,"href=\"/\"")
        }
    }

    @Test
    fun testAbout() = testSuspend {
        testApp.client.get("/about").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testApiEntrypoint() = testSuspend {
        testApp.client.get("/api").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText,"Tech Industry News Analyzer API")
            assertContains(bodyAsText,"\"links\":")
        }
    }

    @Test
    fun testApiTopics() = testSuspend {
        testApp.client.get("/api/topics").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "\"terms\":")
        }
    }
    @Test
    fun testApiArticles() = testSuspend {
        testApp.client.get("/api/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "\"publishedAt\":")
        }
    }

    @Test
    fun testApiArticlesByTopic() = testSuspend {
        testApp.client.get("/api/topics/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText, "\"topic\":")
            assertContains(bodyAsText, "\"articles\":")
            assertContains(bodyAsText, "\"terms\":")
            assertContains(bodyAsText, "\"publishedAt\":")
        }
    }

    @Test
    fun testApiArticlesOnTopic() = testSuspend {
        testApp.client.get("/api/topics/1/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
            val bodyAsText = bodyAsText()
            assertContains(bodyAsText, "\"topic\":")
            assertContains(bodyAsText, "\"topicId\": 1")
            assertContains(bodyAsText, "\"articles\":")
            assertContains(bodyAsText, "\"terms\":")
            assertContains(bodyAsText, "\"publishedAt\":")
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
