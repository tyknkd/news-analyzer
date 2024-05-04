package test.newsanalyzer.dataanalyzer

import io.newsanalyzer.dataanalyzer.plugins.Messaging
import io.newsanalyzer.testsupport.TestSettings
import kotlinx.coroutines.test.runTest
import org.awaitility.kotlin.await
import org.junit.Test
import org.junit.BeforeClass
import org.junit.AfterClass
import kotlin.test.assertEquals

class MessagingTest {
    @Test
    fun testCollectorMq() = runTest {
        val testMessage = "correct collector message"
        Messaging.collectorMessenger.publishMessage(testMessage)
        await.atMost(TestSettings.mqTimeout).untilAsserted {
            assertEquals(testMessage, collectorMqPublished)
        }
    }
    @Test
    fun testAnalyzerMq() = runTest {
        val testMessage = "correct analyzer message"
        Messaging.analyzerMessenger.publishMessage(testMessage)
        await.atMost(TestSettings.mqTimeout).untilAsserted {
            assertEquals(testMessage, analyzerMqPublished)
        }
    }
    companion object {
        private var collectorMqPublished = "wrong collector message"
        private var analyzerMqPublished = "wrong analyzer message"
        private fun collectorMessageHandler(message: String): Boolean {
            collectorMqPublished = message
            return true
        }
        private fun analyzerMessageHandler(message: String): Boolean {
            analyzerMqPublished = message
            return true
        }
        @BeforeClass
        @JvmStatic
        fun setup() {
            Messaging.updateCollectorMessenger(
                exchangeName = "analyzer_app_mq_test_collector_exchange",
                queueName = "analyzer_app_mq_test_collector_queue",
                routingKey = "analyzer_app_mq_test_collector_key",
                messageHandler = ::collectorMessageHandler
            )
            Messaging.collectorMessenger.listen()
            Messaging.updateAnalyzerMessenger(
                exchangeName = "analyzer_app_mq_test_analyzer_exchange",
                queueName = "analyzer_app_mq_test_analyzer_queue",
                routingKey = "analyzer_app_mq_test_analyzer_key",
                messageHandler = ::analyzerMessageHandler
            )
            Messaging.analyzerMessenger.listen()
        }
        @AfterClass
        @JvmStatic
        fun teardown() {
            Messaging.collectorMessenger.delete()
            Messaging.analyzerMessenger.delete()
        }
    }
}