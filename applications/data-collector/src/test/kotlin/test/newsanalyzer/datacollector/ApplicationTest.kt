package test.newsanalyzer.datacollector

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.newsanalyzer.datacollector.plugins.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureSerialization()
            configureDatabases()
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
    fun testArticles() = testApplication {
        application {
            configureSerialization()
            configureDatabases()
            configureRouting()
        }
        client.get("/articles").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}