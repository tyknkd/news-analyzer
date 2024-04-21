package io.newsanalyzer.webserver.plugins

import io.newsanalyzer.mqsupport.Messenger
import io.newsanalyzer.datasupport.models.AnalyzedData
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object Messaging {
    var analyzerMessenger = Messenger(
        exchangeName = System.getenv("ANALYZER_EXCHANGE"),
        queueName = System.getenv("ANALYZER_QUEUE"),
        routingKey = System.getenv("ANALYZER_ROUTING_KEY"),
        messageHandler = ::messageHandler
    )

    private fun messageHandler(message: String): Boolean {
        val data: AnalyzedData = Json.decodeFromString(message)
        val (articles, topics) = data
        return runBlocking { WebDataGateway.updateAll(articles, topics) }
    }

    fun updateAnalyzerMessenger(newMessenger: Messenger) {
        analyzerMessenger = newMessenger
    }
}