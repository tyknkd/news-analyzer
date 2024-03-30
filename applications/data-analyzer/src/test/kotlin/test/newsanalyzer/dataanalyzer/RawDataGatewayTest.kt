package test.newsanalyzer.dataanalyzer

import io.newsanalyzer.dataanalyzer.plugins.*
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

class RawDataGatewayTest {
    private val tables: List<Table> = listOf(RawArticles)
    private val database: Database = DatabaseTemplate("ANALYZER_DB", emptyList()).database
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
    fun testUpsertArticles() = testSuspend {
        val result = RawDataGateway.upsertArticles(TestDoubles.rawArticles)
        assertTrue(result)
        val articles: List<Article> = RawDataGateway.allArticles()
        assertEquals(TestDoubles.rawArticles, articles)
    }
}