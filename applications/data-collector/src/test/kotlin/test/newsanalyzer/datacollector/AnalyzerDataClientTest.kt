package test.newsanalyzer.datacollector

import io.newsanalyzer.datacollector.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.httpsupport.HostPaths
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class AnalyzerDataClientTest {
    private val testDoubles = TestDoubles()
    @Test
    fun testPostArticles() = testApplication {
        // Mock data-analyzer API
        val apiHost = HostPaths().getAnalyzerPath()
        externalServices {
            hosts("http://${apiHost}") {
                install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) { json() }
                routing {
                    post("articles") {
                        call.respondText("Updated", status = HttpStatusCode.OK)
                    }
                }
            }
        }
        val testClient = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { json() }
        }
        val result = AnalyzerDataClient.postArticles(
            articles = testDoubles.rawArticles,
            client = testClient)
        assertTrue(result)
    }
}

