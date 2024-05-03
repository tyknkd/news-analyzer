package test.newsanalyzer.datacollector

import io.newsanalyzer.datacollector.plugins.Messaging
import io.newsanalyzer.testsupport.TestSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class MessagingTest {
    private var mqPublished = "wrong message"
    private fun messageHandler(message: String): Boolean {
        mqPublished = message
        return true
    }
    @BeforeTest
    fun setup() {
        Messaging.updateMessenger(
            exchangeName = "collector_app_mq_test_exchange",
            queueName = "collector_app_mq_test_queue",
            routingKey = "collector_app_mq_test_key",
            messageHandler = ::messageHandler
        )
        Messaging.collectorMessenger.listen()
    }
    @AfterTest
    fun teardown() {
        Messaging.collectorMessenger.delete()
    }
    @Test
    fun testPublishMessage() = runTest {
        val testMessage = "correct message"
        Messaging.collectorMessenger.publishMessage(testMessage)
        delay(TestSettings.mqMinLatency)
        assertEquals(testMessage, mqPublished)
    }
}