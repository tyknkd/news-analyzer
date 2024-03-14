package io.newsanalyzer.webserver.plugins

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.*
import org.jetbrains.kotlinx.dataframe.api.*
import io.newsanalyzer.webserver.models.*
import io.newsanalyzer.webserver.plugins.AnalyzedDatabase.dbQuery

class AnalyzedDataGateway: AnalyzedDAO {
    private fun ResultRow.toArticle() = Article(
        id = this[Articles.id],
        publisher = this[Articles.publisher],
        author = this[Articles.author],
        title = this[Articles.title],
        description = this[Articles.description],
        url = this[Articles.url],
        urlToImage = this[Articles.urlToImage],
        publishedAt = this[Articles.publishedAt],
        content = this[Articles.content],
        topicId = this[Articles.topicId]
    )

    private fun ResultRow.toTopic() = Topic(
        topicId = this[Topics.topicId],
        terms = this[Topics.terms]
    )

    suspend fun addArticles(articles: List<Article>) {
        for (article in articles) {
            dbQuery {
                Articles.insert {
                    it[id] = article.id
                    it[publisher] = article.publisher
                    it[author] = article.author
                    it[title] = article.title
                    it[description] = article.description
                    it[url] = article.url
                    it[urlToImage] = article.urlToImage
                    it[publishedAt] = article.publishedAt
                    it[content] = article.content
                    it[topicId] = article.topicId
                }
            }
        }
    }

    suspend fun addTopics(topics: List<Topic>) {
        for (topic in topics) {
            dbQuery {
                Topics.insert {
                    it[topicId] = topic.topicId
                    it[terms] = topic.terms
                }
            }
        }
    }

    override suspend fun allArticles(): List<Article> = dbQuery {
        Articles.selectAll().map { row -> row.toArticle() }
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
            articlesList.filter{
                article -> article.topicId == "topic"<Topic>().topicId
            }
        }
        return articlesByTopicDf.toListOf<ArticlesByTopic>()
    }
}

val analyzedDataGateway: AnalyzedDAO = AnalyzedDataGateway().apply {
    runBlocking {
        if(allTopics().isEmpty()) {
            val analyzedDataClient = AnalyzedDataClient()
            val (articles, topics) = analyzedDataClient.getAnalyzedData()
            addArticles(articles)
            addTopics(topics)
        }
    }
}