package io.newsanalyzer.datacollector.plugins

import io.newsanalyzer.mqsupport.Messenger

object Messaging {
    var messenger: Messenger = Messenger(
        exchangeName = System.getenv("COLLECTOR_EXCHANGE"),
        queueName = System.getenv("COLLECTOR_QUEUE"),
        routingKey = System.getenv("COLLECTOR_ROUTING_KEY")
    )

    fun updateMessenger(
        exchangeName: String,
        queueName: String,
        routingKey: String
    ) {
        messenger = Messenger(exchangeName, queueName, routingKey)
    }
}