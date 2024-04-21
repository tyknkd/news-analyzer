package io.newsanalyzer.dataanalyzer.plugins

import io.newsanalyzer.mqsupport.Messenger
import io.newsanalyzer.datasupport.models.Article
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json



object Messaging {
    var collectorMessenger = Messenger(
        exchangeName = System.getenv("COLLECTOR_EXCHANGE"),
        queueName = System.getenv("COLLECTOR_QUEUE"),
        routingKey = System.getenv("COLLECTOR_ROUTING_KEY"),
        messageHandler = ::messageHandler
    )

    private fun messageHandler(message: String): Boolean {
        val articles: List<Article> = Json.decodeFromString(message)
        return runBlocking { RawDataGateway.addArticles(articles) }
    }
    fun updateCollectorMessenger(newMessenger: Messenger) {
        collectorMessenger = newMessenger
    }

    var analyzerMessenger = Messenger(
        exchangeName = System.getenv("ANALYZER_EXCHANGE"),
        queueName = System.getenv("ANALYZER_QUEUE"),
        routingKey = System.getenv("ANALYZER_ROUTING_KEY")
    )

    fun updateAnalyzerMessenger(newMessenger: Messenger) {
        analyzerMessenger = newMessenger
    }
}