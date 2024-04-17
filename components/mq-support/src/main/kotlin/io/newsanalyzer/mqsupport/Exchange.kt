package io.newsanalyzer.mqsupport

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel

class Exchange(val name: String, val queue: String, val routingKey: String) {
    private val factory = ConnectionFactory().apply {
        setUri(getRabbitMqUri())
    }
    private val connection = factory.newConnection()
    private val durable = true
    private val exclusive = false
    private val autoDelete = false
    private val arguments: Map<String, Any> = emptyMap()
    val channel: Channel = connection.createChannel().apply {
        exchangeDeclare(name, BuiltinExchangeType.DIRECT)
        queueDeclare(queue, durable, exclusive, autoDelete, arguments)
        queueBind(queue, name, routingKey)
        confirmSelect()
    }

    private fun getRabbitMqUri(): String {
        val port = System.getenv("RABBITMQ_PORT")?:5672
        return if (System.getenv("OS_ENV") == "container") {
            "amqp://mq:$port"
        } else {
            "amqp://localhost:$port"
        }
    }
}
