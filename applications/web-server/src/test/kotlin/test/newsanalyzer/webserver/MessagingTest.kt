package test.newsanalyzer.webserver

import io.newsanalyzer.webserver.plugins.Messaging
import kotlinx.coroutines.launch
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
        Messaging.updateAnalyzerMessenger(
            exchangeName = "webserver_app_test_analyzer_exchange",
            queueName = "webserver_app_test_analyzer_queue",
            routingKey = "webserver_app_test_analyzer_key",
            messageHandler = ::messageHandler
        )
        Messaging.analyzerMessenger.listen()
    }
    @AfterTest
    fun teardown() {
        Messaging.analyzerMessenger.delete()
    }
    @Test
    fun testPublishMessage() = runTest {
        val testMessage = "correct message"
        launch {
            Messaging.analyzerMessenger.publishMessage(testMessage)
        }
        launch {
            assertEquals(testMessage, mqPublished)
        }
    }
}