package test.newsanalyzer.dataanalyzer

import io.newsanalyzer.dataanalyzer.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.datasupport.models.*
import io.ktor.test.dispatcher.*
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
        assertTrue(result,"Data was not added to database")
        val articles: List<Article> = RawDataGateway.allArticles()
        assertEquals(TestDoubles.rawArticles, articles)
    }
}