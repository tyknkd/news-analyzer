package io.newsanalyzer.datacollector.plugins

import io.newsanalyzer.mqsupport.*

object Messaging {
    private val exchangeName = System.getenv("COLLECTOR_EXCHANGE")
    private val queueName = System.getenv("COLLECTOR_QUEUE")
    private val routingKey = System.getenv("COLLECTOR_ROUTING_KEY")
    private val collectorExchange = Exchange(exchangeName, queueName, routingKey)
    private val collectorPublisher = Publisher(collectorExchange)
    fun publishMessage(message: String): Boolean {
        return collectorPublisher.publish(message)
    }
}