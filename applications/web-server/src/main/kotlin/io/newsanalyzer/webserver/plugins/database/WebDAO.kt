package io.newsanalyzer.webserver.plugins.database

import io.newsanalyzer.datasupport.models.*

interface WebDAO {
    suspend fun allArticles(): List<Article>
    suspend fun allTopics(): List<Topic>
    suspend fun allArticlesByTopic(): List<ArticlesByTopic>
    suspend fun articlesOnTopic(topicId: Int): ArticlesByTopic
    suspend fun updateAll(articles: List<Article>, topics: List<Topic>): Boolean
}