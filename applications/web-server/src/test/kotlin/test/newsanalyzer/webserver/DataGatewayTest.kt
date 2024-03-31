package test.newsanalyzer.webserver

import io.newsanalyzer.webserver.plugins.*
import io.newsanalyzer.testsupport.TestDoubles
import io.newsanalyzer.datasupport.models.*
import io.ktor.test.dispatcher.*
import io.newsanalyzer.datasupport.DatabaseTemplate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class DataGatewayTest {
    private val tables: List<Table> = listOf(AnalyzedArticles, Topics)
    private val database: Database = DatabaseTemplate("WEBSERVER_TEST_DB", emptyList()).database
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
    fun testUpdateAll() = testSuspend {
        val firstResult = WebDataGateway.updateAll(TestDoubles.analyzedArticles, TestDoubles.topics)
        assertTrue(firstResult,"No data was added to database")
        val articles: List<Article> = WebDataGateway.allArticles()
        assertEquals(TestDoubles.analyzedArticles, articles)
        val topics: List<Topic> = WebDataGateway.allTopics()
        assertEquals(TestDoubles.topics, topics)

        val secondResult = WebDataGateway.updateAll(TestDoubles.updatedAnalyzedArticles, TestDoubles.updatedTopics)
        assertTrue(secondResult,"No data was added to database")
        val updatedArticles: List<Article> = WebDataGateway.allArticles()
        assertEquals(TestDoubles.updatedAnalyzedArticles, updatedArticles)
        val updatedTopics: List<Topic> = WebDataGateway.allTopics()
        assertEquals(TestDoubles.updatedTopics, updatedTopics)
    }
    @Test
    fun testAllArticlesByTopic() = testSuspend {
        val result = WebDataGateway.updateAll(TestDoubles.updatedAnalyzedArticles, TestDoubles.updatedTopics)
        assertTrue(result,"No data was added to database")
        val articlesByTopic = WebDataGateway.allArticlesByTopic()
        assertEquals(TestDoubles.updatedArticlesByTopic, articlesByTopic)
    }
    @Test
    fun testArticlesOnTopic() = testSuspend {
        val result = WebDataGateway.updateAll(TestDoubles.updatedAnalyzedArticles, TestDoubles.updatedTopics)
        assertTrue(result,"No data was added to database")
        val articlesOnTopic = WebDataGateway.articlesOnTopic(0)
        assertEquals(TestDoubles.updatedArticlesOnTopic, articlesOnTopic)
    }
}