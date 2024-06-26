package test.newsanalyzer.datacollector

import io.newsanalyzer.datacollector.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.test.*

class DataCollectorTest {
    @Test
    fun testCleanData() = testApplication {
        val result = DataCollector.cleanData(TestDoubles.remoteData)
        assertEquals(TestDoubles.filteredSortedRemoteArticles, result)
    }
    @Test
    fun testGetNewsApiData() = testApplication {
        // Mock remote API
        externalServices {
            hosts("https://newsapi.org") {
                install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) { json() }
                routing {
                    get("v2/everything{params}") {
                        call.respond(status = HttpStatusCode.OK, TestDoubles.remoteData)
                    }
                }
            }
        }
        val testClient = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { json() }
        }
        val result = DataCollector.getNewsApiData(
            topic = "some topic",
            sourceList = listOf("publisher-id","another-publisher"),
            newsApiKey = "abc123",
            client = testClient)
        assertEquals(TestDoubles.filteredSortedRemoteArticles, result)
    }
}

