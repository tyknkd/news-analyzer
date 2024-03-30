package test.newsanalyzer.dataanalyzer

import io.newsanalyzer.dataanalyzer.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.httpsupport.HostPaths
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.*

class WebServerClientTest {
    @Test
    fun testPostAnalyzedData() = testApplication {
        externalServices {
            val apiHost = HostPaths().getWebServerPath()
            hosts("http://${apiHost}") {
                install(ContentNegotiation) { json() }
                routing {
                    post("api/update") {
                        call.respondText("Updated", status = HttpStatusCode.OK)
                    }
                }
            }
        }
        val testClient = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { json() }
        }
        val result = WebServerDataClient.postAnalyzedData(TestDoubles.analyzedData, testClient)
        assertTrue(result)
    }
}