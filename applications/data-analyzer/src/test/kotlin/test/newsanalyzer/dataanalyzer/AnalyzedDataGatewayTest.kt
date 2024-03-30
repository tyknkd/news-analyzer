package test.newsanalyzer.dataanalyzer

import io.newsanalyzer.dataanalyzer.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.datasupport.models.*
import io.ktor.test.dispatcher.*
import io.newsanalyzer.datasupport.DatabaseTemplate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class AnalyzedDataGatewayTest {
    private val tables: List<Table> = listOf(AnalyzedArticles, Topics)
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
        val result = AnalyzedDataGateway.upsertArticles(TestDoubles.analyzedArticles)
        assertTrue(result,"No data was added to database")
        val articles: List<Article> = AnalyzedDataGateway.allArticles()
        assertEquals(TestDoubles.analyzedArticles, articles)
    }
    @Test
    fun testUpsertTopics() = testSuspend {
        val result = AnalyzedDataGateway.upsertTopics(TestDoubles.topics)
        assertTrue(result,"Data was not added to database")
        val topics: List<Topic> = AnalyzedDataGateway.allTopics()
        assertEquals(TestDoubles.topics, topics)
    }
}