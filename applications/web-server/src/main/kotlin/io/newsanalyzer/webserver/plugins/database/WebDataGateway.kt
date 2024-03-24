package io.newsanalyzer.webserver.plugins.database

import io.newsanalyzer.datasupport.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.kotlinx.dataframe.api.*
import io.newsanalyzer.datasupport.models.*

object WebDataGateway: AnalyzedArticlesGatewayTemplate, TopicsGatewayTemplate {
    suspend fun updateAll(articles: List<Article>, topics: List<Topic>): Boolean {
        return (upsertArticles(articles) && upsertTopics(topics))
    }

    suspend fun allArticlesByTopic(): List<ArticlesByTopic> {
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

    suspend fun articlesOnTopic(topicId: Int): ArticlesByTopic {
        val topic = topic(topicId)
        val articles = articles(topicId)
        return ArticlesByTopic(topic = topic, articles = articles)
    }
}