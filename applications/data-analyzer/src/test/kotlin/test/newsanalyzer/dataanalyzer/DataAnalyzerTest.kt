package test.newsanalyzer.dataanalyzer

import io.newsanalyzer.dataanalyzer.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.ktor.test.dispatcher.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class DataAnalyzerTest {
    private val tables: List<Table> = listOf(RawArticles)
    private val database: Database = DatabaseTemplate("ANALYZER_TEST_DB", emptyList()).database
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
    fun testGetAnalyzedData() = testSuspend {
        val result = RawDataGateway.upsertArticles(TestDoubles.rawArticles)
        assertTrue(result,"Data was not added to database")
        val (articles, topics) = DataAnalyzer.getAnalyzedData()
        assertEquals(TestDoubles.rawArticles.size, articles.size)
        for (article in articles) {
            assertTrue(article.topicId >= 0)
        }
        assertTrue(topics.size >= 2)
    }
}