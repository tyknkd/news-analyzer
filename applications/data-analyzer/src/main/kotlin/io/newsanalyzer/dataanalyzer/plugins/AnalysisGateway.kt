package io.newsanalyzer.dataanalyzer.plugins

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import io.newsanalyzer.dataanalyzer.models.*
import io.newsanalyzer.dataanalyzer.plugins.AnalyzerDatabase.dbQuery


class AnalysisGateway: AnalysisDAO {
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

    suspend fun updateArticleTopic(id: Int, topicId: Int): Boolean = dbQuery {
        Articles.update({ Articles.id eq id }) {
            it[Articles.id] = id
            it[Articles.topicId] = topicId
        } > 0
    }

    suspend fun clearTopics(): Boolean = dbQuery {
        Topics.deleteAll() > 0
    }

    override suspend fun allArticles(): List<Article> = dbQuery {
        Articles.selectAll().map { row -> row.toArticle() }
    }

    override suspend fun allTopics(): List<Topic> = dbQuery {
        Topics.selectAll().map { row -> row.toTopic() }
    }

    override suspend fun mostRecentDate(): Instant? = dbQuery {
        Articles.selectAll().lastOrNull()?.toArticle()?.publishedAt
    }
}

val analysisGateway: AnalysisDAO = AnalysisGateway().apply {
    runBlocking {
        if(allArticles().isEmpty()) {
            val dataAnalyzer = DataAnalyzer()
            val (articles, topics) = dataAnalyzer.getAnalyzedData()
            addArticles(articles)
            addTopics(topics)
        }
    }
}