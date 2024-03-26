package test.newsanalyzer.datacollector

import io.newsanalyzer.datacollector.plugins.*
import io.newsanalyzer.datacollector.models.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.test.assertEquals

class DataCollectorTest {
    @Test
    fun testCleanData() = testApplication {
        val result = DataCollector.cleanData(mockRemoteData)
        assertEquals(filteredSortedRemoteArticles, result)
    }
    @Test
    fun testGetNewsApiData() = testApplication {
        // Mock remote API
        externalServices {
            hosts("https://newsapi.org") {
                install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) { json() }
                routing {
                    get("v2/everything{params}") {
                        call.respond(status = HttpStatusCode.OK, mockRemoteData)
                    }
                }
            }
        }
        val testClient = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { json() }
        }
        val result = DataCollector.getNewsApiData(
            topic = "some topic",
            sourceList = listOf("publisher-id","another-publisher"),
            newsApiKey = "abc123",
            client = testClient)
        assertEquals(filteredSortedRemoteArticles, result)

    }

    // Mock data
    private val remoteArticle1 = RemoteArticle(
        source = RemoteSource(
            id = "publisher-id",
            name = "Publisher Name",
        ),
        author = "John Doe",
        title = "An article title",
        description = "A description of the article",
        url = "https://www.example.com/article",
        urlToImage = "https://example.com/123456abcdef?width=1200&format=jpeg",
        publishedAt = "2024-02-20T14:13:51Z",
        content = "Example/Organization\r\n<ul><li>Some note about this second article.</li><li>Some more info about the second article.</li></ul>And now the content begins,… [+3569 chars]",
    )
    private val remoteArticle2 = RemoteArticle(
        source = RemoteSource(
            id = "another-publisher",
            name = "Another Publisher",
        ),
        author = "Jane Doe",
        title = "Another article title",
        description = "A description of another article",
        url = "https://www.example.com/article2",
        urlToImage = "https://example.com/123456abcdef?width=1200&format=jpeg",
        publishedAt = "2024-02-21T00:06:38Z",
        content = "This article's content… [+4506 chars]",
    )
    private val removedRemoteArticle = RemoteArticle(
        source = RemoteSource(
            id = "null",
            name = "[Removed]"
        ),
        author = "",
        title = "[Removed]",
        description = "[Removed]",
        url = "https://removed.com",
        urlToImage = "",
        publishedAt = "1970-01-01T00:00:00Z",
        content = "[Removed]"
    )
    private val remoteArticleMissingInfo = RemoteArticle(
        source = RemoteSource(
            id = "publisher-id",
            name = "Publisher Name"
        ),
        author = "",
        title = "",
        description = "This is an article without a title or author.",
        url = "https://www.example.com/article4",
        urlToImage = "",
        publishedAt = "1970-01-01T00:00:00Z",
        content = "This article's content… [+3102 chars]"
    )
    private val mockRemoteData = RemoteData(
        status = "ok",
        totalResults = 4,
        articles = listOf(remoteArticle1, remoteArticle2, removedRemoteArticle, remoteArticleMissingInfo)
    )
    private val filteredSortedRemoteArticles = listOf(remoteArticle2, remoteArticle1)
}

