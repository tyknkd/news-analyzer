package io.newsanalyzer.dataanalyzer.plugins.database

import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import io.newsanalyzer.dataanalyzer.models.*
import io.newsanalyzer.dataanalyzer.plugins.DataAnalyzer
import io.newsanalyzer.dataanalyzer.plugins.database.AnalyzerDatabase.dbQuery


object AnalyzedDataGateway: AnalyzedDAO {

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

    private suspend fun upsertArticles(articles: List<Article>) {
        dbQuery {
            val onUpdateExclude = (AnalyzedArticles.columns.toSet() - AnalyzedArticles.topicId).toList()
            AnalyzedArticles.batchUpsert(data = articles, onUpdateExclude = onUpdateExclude) {
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

    override suspend fun updateAll(): Boolean {
        upsertAll()
        return true
    }

    override suspend fun allArticles(): List<Article> = dbQuery {
        AnalyzedArticles.selectAll().map { row -> row.toArticle() }
    }

    override suspend fun allTopics(): List<Topic> = dbQuery {
        Topics.selectAll().map { row -> row.toTopic() }
    }

    override suspend fun mostRecentDate(): Instant? = dbQuery {
        AnalyzedArticles.selectAll().lastOrNull()?.toArticle()?.publishedAt
    }
}