package test.newsanalyzer.mqsupport

import io.newsanalyzer.mqsupport.Messenger
import io.newsanalyzer.testsupport.TestSettings
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import org.awaitility.kotlin.await

class MqTest {
    private var response = "wrong message"
    private lateinit var messenger: Messenger
    private fun messageHandler(message: String): Boolean {
        response = message
        return true
    }
    @BeforeTest
    fun setup() {
        messenger = Messenger(
            exchangeName = "mq_test_exchange",
            queueName = "mq_test_queue",
            routingKey = "mq_test_key",
            messageHandler = ::messageHandler
        )
        messenger.listen()
    }
    @AfterTest
    fun teardown() {
        messenger.delete()
    }
    @Test
    fun testMessageQueue() = runTest {
        val testMessage = "correct message"
        messenger.publishMessage(testMessage)
        await.atMost(TestSettings.mqTimeout).untilAsserted {
            assertEquals(testMessage, response)
        }
        val secondMessage = "second message"
        messenger.publishMessage(secondMessage)
        await.atMost(TestSettings.mqTimeout).untilAsserted {
            assertEquals(secondMessage, response)
        }
    }
}