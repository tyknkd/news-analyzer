package test.newsanalyzer.webserver

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.newsanalyzer.webserver.plugins.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureTemplating()
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
    @Test
    fun testHealth() = testApplication {
        application {
            configureRouting()
        }
        client.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("OK", bodyAsText())
        }
    }
    @Test
    fun testAPI() = testApplication {
        application {
            configureRouting()
        }
        client.get("/api").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("News Analyzer API", bodyAsText())
        }
    }
    @Test
    fun testStatic() = testApplication {
        application {
            configureRouting()
        }
        client.get("/static").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
    @Test
    fun testArticles() = testApplication {
        application {
            configureTemplating()
            configureRouting()
        }
        client.get("/articles/0").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
