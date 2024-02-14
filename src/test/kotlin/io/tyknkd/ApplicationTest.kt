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
        client.get("/api").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("News Analyzer API", bodyAsText())
        }
    }
}
