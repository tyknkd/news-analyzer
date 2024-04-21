package io.newsanalyzer.mqsupport

class Messenger(
    val exchangeName: String,
    val queueName: String,
    val routingKey: String,
    val messageHandler: (message: String) -> Boolean = { _ -> true }
) {
    private val exchange = Exchange(exchangeName, queueName, routingKey)
    private val publisher = Publisher(exchange)
    private val consumer = Consumer(exchange)
    fun publishMessage(message: String): Boolean { return publisher.publish(message) }
    fun listen() { return consumer.consume(messageHandler)}
}