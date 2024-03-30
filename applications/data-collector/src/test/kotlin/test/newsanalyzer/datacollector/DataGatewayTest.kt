package test.newsanalyzer.datacollector

import io.newsanalyzer.datacollector.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.httpsupport.HostPaths
import io.ktor.http.*
import io.ktor.test.dispatcher.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.newsanalyzer.datasupport.DatabaseTemplate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class DataGatewayTest {
    private val tables: List<Table> = listOf(RawArticles)
    private val database: Database = DatabaseTemplate("COLLECTOR_DB", emptyList()).database
    @BeforeTest
    fun setup() {
        transaction(database) {
            for (table in tables) {
                SchemaUtils.create(table)
            }
        }
    }
    @AfterTest
    fun teardown() {
        transaction(database) {
            for (table in tables) {
                SchemaUtils.drop(table)
            }
        }
    }

    @Test
    fun testAddRemoteArticles() = testSuspend {
        val result = CollectorDataGateway.addRemoteArticles(TestDoubles.filteredSortedRemoteArticles)
        assertTrue(result,"Data was not added to database")
        val articles: List<Article> = CollectorDataGateway.allArticles()
        assertEquals(TestDoubles.rawArticles, articles)
    }

    @Test
    fun testArticlesAfter() = testSuspend {
        val result = CollectorDataGateway.addRemoteArticles(TestDoubles.filteredSortedRemoteArticles + TestDoubles.remoteArticlesUpdate)
        assertTrue(result,"Data was not added to database")
        val articles: List<Article> = CollectorDataGateway.articlesAfter(TestDoubles.secondArticlePublishedAt)
        assertEquals(TestDoubles.rawArticlesUpdateOnly, articles)
    }
    @Test
    fun testLatestDateTime() = testSuspend {
        val result = CollectorDataGateway.addRemoteArticles(TestDoubles.filteredSortedRemoteArticles)
        assertTrue(result,"Data was not added to database")
        val latestDateTime = CollectorDataGateway.latestDateTime()
        assertEquals(TestDoubles.secondArticlePublishedAt, latestDateTime)
    }
    @Test
    fun testUpdateArticles() = testApplication {
        externalServices {
            hosts("https://newsapi.org") {
                install(ContentNegotiation) { json() }
                routing {
                    get("v2/everything{params}") {
                        call.respond(status = HttpStatusCode.OK, TestDoubles.remoteData)
                    }
                }
            }
            val apiHost = HostPaths().getAnalyzerPath()
            hosts("http://${apiHost}") {
                install(ContentNegotiation) { json() }
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
        CollectorDataGateway.updateClient(testClient)
        val firstResult = CollectorDataGateway.addRemoteArticles(TestDoubles.filteredSortedRemoteArticles)
        assertTrue(firstResult,"Data was not added to database")
        val secondResult = CollectorDataGateway.addRemoteArticles(TestDoubles.remoteArticlesUpdate)
        assertTrue(secondResult,"Data was not added to database")
        val articles: List<Article> = CollectorDataGateway.allArticles()
        assertEquals(TestDoubles.updatedRawArticles, articles)
    }
}