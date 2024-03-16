package io.newsanalyzer.webserver.plugins.database

import io.newsanalyzer.webserver.models.*

interface AnalyzedDAO {
    suspend fun allArticles(): List<Article>
    suspend fun allTopics(): List<Topic>
    suspend fun allArticlesByTopic(): List<ArticlesByTopic>
    suspend fun articlesOnTopic(topicId: Int): ArticlesByTopic
}