package io.newsanalyzer.webserver.plugins

import io.newsanalyzer.mqsupport.*
import io.newsanalyzer.datasupport.models.AnalyzedData
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object Messaging {
    private val analyzerExchangeName = System.getenv("ANALYZER_EXCHANGE")
    private val analyzerQueueName = System.getenv("ANALYZER_QUEUE")
    private val analyzerRoutingKey = System.getenv("ANALYZER_ROUTING_KEY")

    private val analyzerExchange = Exchange(analyzerExchangeName, analyzerQueueName, analyzerRoutingKey)
    private val analyzerConsumer = Consumer(analyzerExchange)

    private fun messageHandler(message: String): Boolean {
        val data: AnalyzedData = Json.decodeFromString(message)
        val (articles, topics) = data
        return runBlocking { WebDataGateway.updateAll(articles, topics) }
    }
    fun listen() {
        return analyzerConsumer.consume(::messageHandler)
    }
}