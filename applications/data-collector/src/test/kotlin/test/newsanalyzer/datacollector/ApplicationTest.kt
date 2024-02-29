package test.newsanalyzer.datacollector

import io.newsanalyzer.datacollector.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.junit.Test
import org.junit.BeforeClass
import org.junit.AfterClass
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertContains
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ApplicationTest {
    private val apiKeyErrorMessage = "NEWS_API_KEY environment variable is invalid or not set. Check your key, or obtain a free key from https://newsapi.org"
    @Test
    fun testApiKey() {
        val newsApiKey = if (System.getenv("OS_ENV") == "container") {
            Files.readAllBytes(Paths.get(System.getenv("NEWS_API_KEY_FILE"))).toString()
        } else {
            System.getenv("NEWS_API_KEY")
        }
        assertNotNull(newsApiKey, apiKeyErrorMessage)
        assertNotEquals(newsApiKey,"yournewsapikeygoeshere", apiKeyErrorMessage)
    }
    @Test
    fun testArticles() = testSuspend {
        testApp.client.get("/articles").apply {
            assertEquals(HttpStatusCode.OK, status, apiKeyErrorMessage)
            assertContains(bodyAsText(), "\"publishedAt\":")
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
    fun testRoot() = testSuspend {
        testApp.client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status, apiKeyErrorMessage)
            assertContains(bodyAsText(), "\"publishedAt\":")
        }
    }


    companion object {
        lateinit var testApp: TestApplication
        @JvmStatic
        @BeforeClass
        fun setup() {
            testApp = TestApplication {
                application {
                    configureSerialization()
                    configureDatabases()
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