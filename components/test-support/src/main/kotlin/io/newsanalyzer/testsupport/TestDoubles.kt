package io.newsanalyzer.testsupport

import io.newsanalyzer.datasupport.models.*
import kotlinx.datetime.Instant

object TestDoubles {
    private val remoteArticle1 = RemoteArticle(
        source = RemoteSource(
            id = "publisher-id",
            name = "Publisher Name",
        ),
        author = "John Doe",
        title = "A catchy article title",
        description = "An attention-grabbing description of the article",
        url = "https://www.example.com/article",
        urlToImage = "https://example.com/123456abcdef?width=1200&format=jpeg",
        publishedAt = "2024-02-20T14:13:51Z",
        content = "Example/Organization\r\n<ul><li>Some note about this article.</li><li>Some more info about the article.</li></ul>And now the content begins,… [+3569 chars]",
    )
    private val remoteArticle2 = RemoteArticle(
        source = RemoteSource(
            id = "another-publisher",
            name = "Another Publisher",
        ),
        author = "Jane Doe",
        title = "Another article title",
        description = "An interesting description of another article",
        url = "https://www.example.com/another-article",
        urlToImage = "https://example.com/123456abcdef?width=1200&format=jpeg",
        publishedAt = "2024-02-21T00:06:38Z",
        content = "This second article's content is so on and so forth… [+4506 chars]",
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
        title = "Yet another clickbait article title",
        description = "Yet another interesting description of yet another article",
        url = "https://www.example.com/yetanotherarticle",
        urlToImage = "https://example.com/123456abcdef?width=1200&format=jpeg",
        publishedAt = "2024-02-23T00:00:12Z",
        content = "This third article's content goes like this… [+4506 chars]",
    )
    val remoteArticlesUpdate = listOf(remoteArticle3)
    private fun buildRawArticlesList(remoteArticles: List<RemoteArticle>, firstId: Int = 1): List<Article> {
        val rawArticles = emptyList<Article>().toMutableList()
        var id = firstId
        for (remoteArticle in remoteArticles) {
            rawArticles += Article(
                id = id,
                publisher = remoteArticle.source.name,
                author = remoteArticle.author,
                title = remoteArticle.title,
                description = remoteArticle.description,
                url = remoteArticle.url,
                urlToImage = remoteArticle.urlToImage,
                publishedAt = Instant.parse(remoteArticle.publishedAt),
                content = remoteArticle.content,
                topicId = -1
            )
            id++
        }
        return rawArticles.toList()
    }
    val rawArticles = buildRawArticlesList(filteredSortedRemoteArticles)
    val rawArticlesUpdateOnly = buildRawArticlesList(remoteArticlesUpdate,3)
    val updatedRawArticles = rawArticles + rawArticlesUpdateOnly
    private fun buildAnalyzedArticlesList(rawArticles: List<Article>, firstId: Int = 1, firstTopicId: Int = 0): List<Article> {
        val analyzedArticles = emptyList<Article>().toMutableList()
        var id = firstId
        var topicId = firstTopicId
        for (rawArticle in rawArticles) {
            analyzedArticles += Article(
                id = id,
                publisher = rawArticle.publisher,
                author = rawArticle.author,
                title = rawArticle.title,
                description = rawArticle.description,
                url = rawArticle.url,
                urlToImage = rawArticle.urlToImage,
                publishedAt = rawArticle.publishedAt,
                content = rawArticle.content,
                topicId = topicId // arbitrarily assigned
            )
            id++
            topicId++
        }
        return analyzedArticles.toList()
    }
    val analyzedArticles = buildAnalyzedArticlesList(rawArticles)
    private val analyzedArticlesUpdateOnly = buildAnalyzedArticlesList(rawArticlesUpdateOnly, 3, 1)
    val updatedAnalyzedArticles = analyzedArticles + analyzedArticlesUpdateOnly

    // Note: topic terms are arbitrary examples; actual terms/order may vary
    val topics = listOf(
        Topic(
            topicId = 0,
            terms = "[article, description, title, content, second, interesting, grabbing, begins, organization, catchy]"
        ),
        Topic(
            topicId = 1,
            terms = "[article, content, title, description, attention, example, info, note, catchy, organization]"
        )
    )
    val updatedTopics = listOf(
        Topic(
            topicId = 0,
            terms = "[article, title, description, content, interesting, second, like, clickbait, goes, info]"
        ),
        Topic(
            topicId = 1,
            terms = "[article, content, description, title, interesting, grabbing, example, begins, attention, catchy]"
        )
    )
    val analyzedData = AnalyzedData(analyzedArticles, topics)
}