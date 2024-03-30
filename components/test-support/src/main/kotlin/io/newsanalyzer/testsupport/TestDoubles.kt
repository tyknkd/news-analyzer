package io.newsanalyzer.testsupport

import io.newsanalyzer.datasupport.models.*
import kotlinx.datetime.Instant

object TestDoubles {
    private val remoteArticle2 = RemoteArticle(
        source = RemoteSource(
            id = "another-publisher",
            name = "Another Publisher",
        ),
        author = "Jane Doe",
        title = "Another article title",
        description = "A description of another article",
        url = "https://www.example.com/another-article",
        urlToImage = "https://example.com/123456abcdef?width=1200&format=jpeg",
        publishedAt = "2024-02-21T00:06:38Z",
        content = "This article's content… [+4506 chars]",
    )
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
        url = "https://www.example.com/missinginfoarticle",
        urlToImage = "",
        publishedAt = "1970-01-01T00:00:00Z",
        content = "This article's content… [+3102 chars]"
    )
    val remoteData = RemoteData(
        status = "ok",
        totalResults = 4,
        articles = listOf(remoteArticle2, remoteArticle1, removedRemoteArticle, remoteArticleMissingInfo)
    )
    val filteredSortedRemoteArticles = listOf(remoteArticle1, remoteArticle2)
    val secondArticlePublishedAt = Instant.parse(remoteArticle2.publishedAt)
    private val remoteArticle3 = RemoteArticle(
        source = RemoteSource(
            id = "another-publisher",
            name = "Another Publisher",
        ),
        author = "Nancy Drew",
        title = "Yet another article title",
        description = "Yet another description of yet another article",
        url = "https://www.example.com/yetanotherarticle",
        urlToImage = "https://example.com/123456abcdef?width=1200&format=jpeg",
        publishedAt = "2024-02-23T00:00:12Z",
        content = "This other article's content… [+4506 chars]",
    )
    val remoteArticlesUpdate = listOf(remoteArticle3)
    private fun buildArticlesList(remoteArticles: List<RemoteArticle>, firstIdx: Int = 1, topicId: Int = -1): List<Article> {
        val rawArticles = emptyList<Article>().toMutableList()
        var counter = firstIdx
        for (remoteArticle in remoteArticles) {
            rawArticles += Article(
                id = counter,
                publisher = remoteArticle.source.name,
                author = remoteArticle.author,
                title = remoteArticle.title,
                description = remoteArticle.description,
                url = remoteArticle.url,
                urlToImage = remoteArticle.urlToImage,
                publishedAt = Instant.parse(remoteArticle.publishedAt),
                content = remoteArticle.content,
                topicId = topicId
            )
            counter++
        }
        return rawArticles.toList()
    }
    val rawArticles = buildArticlesList(filteredSortedRemoteArticles,1, -1)
    val rawArticlesUpdateOnly = buildArticlesList(remoteArticlesUpdate,3, -1)
    val updatedRawArticles = rawArticles + rawArticlesUpdateOnly
    val analyzedArticles = buildArticlesList(filteredSortedRemoteArticles, 1,0)
    val updatedAnalyzedArticles = buildArticlesList(filteredSortedRemoteArticles + remoteArticlesUpdate, 1,0)
    val topics = listOf(
        Topic(
            topicId = 0,
            terms = "some, terms"
        )
    )
}