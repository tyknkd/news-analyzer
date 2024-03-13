package io.newsanalyzer.dataanalyzer.plugins

import io.newsanalyzer.dataanalyzer.models.*
import org.jetbrains.kotlinx.dataframe.api.*

class DataRestructuring {
    suspend fun groupArticlesByTopic(): List<ArticlesByTopic> {
        val topicsDf = analysisGateway.allTopics()
            .toColumnOf<Topic>("topic")
            .toDataFrame()
        val articlesList = analysisGateway.allArticles()
            .sortedByDescending { article -> article.publishedAt }
        val articlesByTopicDf = topicsDf.add("articles") {
            articlesList.filter{ article -> article.topicId == "topic"<Topic>().topicId }
        }
        return articlesByTopicDf.toListOf<ArticlesByTopic>()
    }
}

val dataRestructuring = DataRestructuring()