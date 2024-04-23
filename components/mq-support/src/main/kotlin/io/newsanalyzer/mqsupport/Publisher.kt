package io.newsanalyzer.mqsupport

import com.rabbitmq.client.MessageProperties
import java.io.IOException
import java.util.concurrent.TimeoutException

class Publisher(private val exchange: Exchange) {
    fun publish(message: String): Boolean {
        try {
            exchange.channel.basicPublish(
                exchange.name,
                exchange.routingKey,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.toByteArray()
            )
            exchange.channel.waitForConfirmsOrDie(5_000)
            mqLogger.debug(" Sent on '${exchange.queue}' with '${exchange.routingKey}' key: '$message'")
            return true
        } catch (exception: TimeoutException) {
            mqLogger.warn(" Caught exception: $exception. Message not published.")
            return false
        } catch (exception: IOException) {
            mqLogger.warn(" Caught exception: $exception. Message not published.")
            return false
        }
    }
}