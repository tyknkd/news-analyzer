package io.newsanalyzer.webserver.plugins.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.kotlinx.dataframe.api.*
import io.newsanalyzer.datasupport.models.*
import io.newsanalyzer.webserver.plugins.database.WebDatabase.dbQuery

object WebDataGateway: WebDAO {

    private fun ResultRow.toArticle() = Article(
        id = this[AnalyzedArticles.id],
        publisher = this[AnalyzedArticles.publisher],
        author = this[AnalyzedArticles.author],
        title = this[AnalyzedArticles.title],
        description = this[AnalyzedArticles.description],
        url = this[AnalyzedArticles.url],
        urlToImage = this[AnalyzedArticles.urlToImage],
        publishedAt = this[AnalyzedArticles.publishedAt],
        content = this[AnalyzedArticles.content],
        topicId = this[AnalyzedArticles.topicId]
    )

    private fun ResultRow.toTopic() = Topic(
        topicId = this[Topics.topicId],
        terms = this[Topics.terms]
    )

    private suspend fun upsertArticles(articles: List<Article>): Boolean {
        var success = false
        dbQuery {
            val onUpdateExclude = AnalyzedArticles.columns - setOf(AnalyzedArticles.topicId)
            val results = AnalyzedArticles.batchUpsert(data = articles, onUpdateExclude = onUpdateExclude) {
                (id, publisher, author, title, description, url,
                    urlToImage, publishedAt, content, topicId) ->
                this[AnalyzedArticles.id] = id
                this[AnalyzedArticles.publisher] = publisher
                this[AnalyzedArticles.author] = author
                this[AnalyzedArticles.title] = title
                this[AnalyzedArticles.description] = description
                this[AnalyzedArticles.url] = url
                this[AnalyzedArticles.urlToImage] = urlToImage
                this[AnalyzedArticles.publishedAt] = publishedAt
                this[AnalyzedArticles.content] = content
                this[AnalyzedArticles.topicId] = topicId
            }
            success = results.isNotEmpty()
        }
        return success
    }

    private suspend fun upsertTopics(topics: List<Topic>): Boolean {
        var success = false
        dbQuery {
            val onUpdateExclude = Topics.columns - setOf(Topics.terms)
            val results = Topics.batchUpsert(data = topics, onUpdateExclude = onUpdateExclude) {
                    (topicId, terms) ->
                this[Topics.topicId] = topicId
                this[Topics.terms] = terms
            }
            success = results.isNotEmpty()
        }
        return success
    }

    override suspend fun updateAll(articles: List<Article>, topics: List<Topic>): Boolean {
        return (upsertArticles(articles) && upsertTopics(topics))
    }

    override suspend fun allArticles(): List<Article> = dbQuery {
        AnalyzedArticles.selectAll().map { row -> row.toArticle() }
    }

    override suspend fun allTopics(): List<Topic> = dbQuery {
        Topics.selectAll().map { row -> row.toTopic() }
    }

    override suspend fun allArticlesByTopic(): List<ArticlesByTopic> {
        val topicsDf = allTopics()
            .toColumnOf<Topic>("topic")
            .toDataFrame()
        val articlesList = allArticles()
            .sortedByDescending { article -> article.publishedAt }
        val articlesByTopicDf = topicsDf.add("articles") {
            articlesList.filter {
                article -> article.topicId == "topic"<Topic>().topicId
            }
        }
        return articlesByTopicDf.toListOf<ArticlesByTopic>()
    }

    private suspend fun articles(topicId: Int): List<Article> = dbQuery {
        AnalyzedArticles
            .selectAll()
            .where { AnalyzedArticles.topicId eq topicId }
            .map { row -> row.toArticle() }
            .sortedByDescending { article -> article.publishedAt }
    }

    private suspend fun topic(topicId: Int): Topic = dbQuery {
        Topics
            .selectAll()
            .where { Topics.topicId eq topicId }
            .map { row -> row.toTopic() }
            .first()
    }

    override suspend fun articlesOnTopic(topicId: Int): ArticlesByTopic {
        val topic = topic(topicId)
        val articles = articles(topicId)
        return ArticlesByTopic(topic = topic, articles = articles)
    }
}