package test.newsanalyzer.mqsupport

import io.newsanalyzer.mqsupport.Messenger
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.*

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
    fun testMessaging() = runTest {
        val testMessage = "correct message"
        launch {
            messenger.publishMessage(testMessage)
        }
        launch {
            assertEquals(testMessage, response)
        }
    }
}