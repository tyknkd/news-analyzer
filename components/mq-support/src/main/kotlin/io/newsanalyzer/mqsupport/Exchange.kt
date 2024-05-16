package io.newsanalyzer.mqsupport

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel

class Exchange(val name: String, val queue: String, val routingKey: String) {
    private val user = System.getenv("RABBITMQ_DEFAULT_USER")
    private val pass = System.getenv("RABBITMQ_DEFAULT_PASS")
    private val mqport = System.getenv("RABBITMQ_PORT")?.toInt()?:5672
    private val mqhost = if (System.getenv("OS_ENV") == "container") {
        System.getenv("RABBITMQ_HOST")
    } else {
        "localhost"
    }
    private val factory = ConnectionFactory().apply {
        username = user
        password = pass
        port = mqport
        host = mqhost
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
}
