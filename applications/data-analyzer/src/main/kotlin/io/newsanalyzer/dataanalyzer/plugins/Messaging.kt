package io.newsanalyzer.dataanalyzer.plugins

import io.newsanalyzer.mqsupport.*
import io.newsanalyzer.datasupport.models.Article
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object Messaging {
    private val collectorExchangeName = System.getenv("COLLECTOR_EXCHANGE")
    private val collectorQueueName = System.getenv("COLLECTOR_QUEUE")
    private val collectorRoutingKey = System.getenv("COLLECTOR_ROUTING_KEY")

    private val collectorExchange = Exchange(collectorExchangeName, collectorQueueName, collectorRoutingKey)
    private val collectorConsumer = Consumer(collectorExchange)

    private fun messageHandler(message: String): Boolean {
        val articles: List<Article> = Json.decodeFromString(message)
        return runBlocking { RawDataGateway.addArticles(articles) }
    }

    fun listen() {
        return collectorConsumer.consume(::messageHandler)
    }

    private val analyzerExchangeName = System.getenv("ANALYZER_EXCHANGE")
    private val analyzerQueueName = System.getenv("ANALYZER_QUEUE")
    private val analyzerRoutingKey = System.getenv("ANALYZER_ROUTING_KEY")

    private val analyzerExchange = Exchange(analyzerExchangeName, analyzerQueueName, analyzerRoutingKey)
    private val analyzerPublisher = Publisher(analyzerExchange)

    fun publishMessage(message: String): Boolean {
        return analyzerPublisher.publish(message)
    }
}