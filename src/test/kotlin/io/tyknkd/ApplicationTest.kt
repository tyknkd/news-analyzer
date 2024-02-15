package io.tyknkd

import io.tyknkd.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
    @Test
    fun testHealth() = testApplication {
        client.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("OK", bodyAsText())
        }
    }
    @Test
    fun testAPI() = testApplication {
        client.get("/api").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("News Analyzer API", bodyAsText())
        }
    }
    @Test
    fun testStatic() = testApplication {
        client.get("/static").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
    @Test
    fun testArticles() = testApplication {
        client.get("/articles/0").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
