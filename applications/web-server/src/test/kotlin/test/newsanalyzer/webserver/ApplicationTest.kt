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
    fun testArticles() = testSuspend {
        testApp.client.get("/articles/0").apply {
            assertEquals(HttpStatusCode.OK, status)
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
