package io.newsanalyzer.mqsupport

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery

class Consumer(private val exchange: Exchange) {
    fun consume(messageHandler: (message: String) -> Boolean = { _ -> true }) {
        val autoAcknowledge = false
        val deliverCallback = DeliverCallback { _: String?, delivery: Delivery ->
            val message = delivery.body.decodeToString()
            logger.debug(" Received on '${delivery.envelope.exchange}' with '${delivery.envelope.routingKey}' key: '$message'")
            if (messageHandler(message)) {
                exchange.channel.basicAck(delivery.envelope.deliveryTag, false)
            }
        }
        val cancelCallback = CancelCallback { _: String? ->
            logger.warn(" Received cancel call")
        }
        exchange.channel.basicConsume(exchange.queue, autoAcknowledge, deliverCallback, cancelCallback)
    }
}