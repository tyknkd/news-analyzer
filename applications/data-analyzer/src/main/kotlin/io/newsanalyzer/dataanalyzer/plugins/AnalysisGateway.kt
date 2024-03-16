package io.newsanalyzer.dataanalyzer.plugins

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import io.newsanalyzer.dataanalyzer.models.*
import io.newsanalyzer.dataanalyzer.plugins.AnalyzerDatabase.dbQuery


object AnalysisGateway: AnalysisDAO {
    fun init() {
        runBlocking {
            if(allArticles().isEmpty()) {
                upsertAll()
            }
        }
    }
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

    private suspend fun upsertArticles(articles: List<Article>) {
        dbQuery {
            val onUpdateExclude = (Articles.columns.toSet() - Articles.topicId).toList()
            Articles.batchUpsert(data = articles, onUpdateExclude = onUpdateExclude) {
                    (id, publisher, author, title, description, url,
                        urlToImage, publishedAt, content, topicId) ->
                this[Articles.id] = id
                this[Articles.publisher] = publisher
                this[Articles.author] = author
                this[Articles.title] = title
                this[Articles.description] = description
                this[Articles.url] = url
                this[Articles.urlToImage] = urlToImage
                this[Articles.publishedAt] = publishedAt
                this[Articles.content] = content
                this[Articles.topicId] = topicId
            }
        }
    }

    private suspend fun upsertTopics(topics: List<Topic>) {
        dbQuery {
            val onUpdateExclude = (Topics.columns.toSet() - Topics.terms).toList()
            Topics.batchUpsert(data = topics, onUpdateExclude = onUpdateExclude) {
                    (topicId, terms) ->
                this[Topics.topicId] = topicId
                this[Topics.terms] = terms
            }
        }
    }

    private suspend fun upsertAll() {
        val (articles, topics) = DataAnalyzer.getAnalyzedData()
        upsertArticles(articles)
        upsertTopics(topics)
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