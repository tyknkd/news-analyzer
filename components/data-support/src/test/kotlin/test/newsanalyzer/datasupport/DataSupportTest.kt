package test.newsanalyzer.datasupport

import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.datasupport.DatabaseTemplate
import io.ktor.test.dispatcher.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class DataSupportTest {
    private val tables: List<Table> = listOf(Topics)
    private val database: Database = DatabaseTemplate(System.getenv("ANALYZER_TEST_DB"), emptyList()).database
    private val topic = Topic(
        topicId = 0,
        terms = "[article, description, title, content, second, interesting, grabbing, begins, organization, catchy]"
    )
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
    fun testDatabaseTemplate() = testSuspend {
        lateinit var result: String
        transaction {
            Topics.insert {
                it[topicId] = topic.topicId
                it[terms] = topic.terms
            }
            result = Topics.selectAll().single()[Topics.terms]
        }
        assertEquals(topic.terms, result)
    }
}