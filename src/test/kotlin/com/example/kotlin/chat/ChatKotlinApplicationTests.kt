package com.example.kotlin.chat

import com.example.kotlin.chat.extensions.prepareForTesting
import com.example.kotlin.chat.repository.MessageRepository
import com.example.kotlin.chat.repository.domain.ContentType
import com.example.kotlin.chat.repository.domain.Message
import com.example.kotlin.chat.service.vm.MessageVM
import com.example.kotlin.chat.service.vm.UserVM
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient
import java.net.URL
import java.time.Instant
import java.time.temporal.ChronoUnit.MILLIS

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = [
            "spring.r2dbc.url=r2dbc:h2:mem:///testdb;USER=sa;PASSWORD=password"
        ]
)
class ChatKotlinApplicationTests {

    @Autowired
    lateinit var client: WebTestClient

    @Autowired
    lateinit var messageRepository: MessageRepository

    lateinit var lastMessageId: String

    val now: Instant = Instant.now()

    @BeforeEach
    fun setUp() {
        runBlocking {
            val secondBeforeNow = now.minusSeconds(1)
            val twoSecondBeforeNow = now.minusSeconds(2)
            val savedMessages = messageRepository.saveAll(listOf(
                    Message(
                            "*testMessage*",
                            ContentType.PLAIN,
                            twoSecondBeforeNow,
                            "test",
                            "http://test.com"
                    ),
                    Message(
                            "**testMessage2**",
                            ContentType.MARKDOWN,
                            secondBeforeNow,
                            "test1",
                            "http://test.com"
                    ),
                    Message(
                            "`testMessage3`",
                            ContentType.MARKDOWN,
                            now,
                            "test2",
                            "http://test.com"
                    )
            )).toList()
            lastMessageId = savedMessages.first().id ?: ""
        }
    }

    @AfterEach
    fun tearDown() {
        runBlocking {
            messageRepository.deleteAll()
        }
    }

    @Test
    fun contextLoads() {
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `test that messages API returns latest messages`(withLastMessageId: Boolean) {
        val messages: List<MessageVM>? = client
                .get()
                .uri("/api/v1/messages?lastMessageId=${if (withLastMessageId) lastMessageId else ""}")
                .exchange()
                .expectBody(object : ParameterizedTypeReference<List<MessageVM>>() {})
                .returnResult()
                .responseBody

        if (!withLastMessageId) {
            assertThat(messages?.map { it.prepareForTesting() })
                    .first()
                    .isEqualTo(MessageVM(
                            "*testMessage*",
                            UserVM("test", URL("http://test.com")),
                            now.minusSeconds(2).truncatedTo(MILLIS)
                    ))
        }

        assertThat(messages?.map { it.prepareForTesting() })
                .containsSubsequence(
                        MessageVM(
                                "<body><p><strong>testMessage2</strong></p></body>",
                                UserVM("test1", URL("http://test.com")),
                                now.minusSeconds(1).truncatedTo(MILLIS)
                        ),
                        MessageVM(
                                "<body><p><code>testMessage3</code></p></body>",
                                UserVM("test2", URL("http://test.com")),
                                now.truncatedTo(MILLIS)
                        )
                )
    }

    @Test
    fun `test that messages posted to the API is stored`() {
        runBlocking {
            client.post()
                    .uri("/api/v1/messages")
                    .bodyValue(MessageVM(
                            "`HelloWorld`",
                            UserVM("test", URL("http://test.com")),
                            now.plusSeconds(1)
                    ))
                    .exchange()
                    .expectStatus()
                    .is2xxSuccessful

            messageRepository.findAll()
                    .first { it.content.contains("HelloWorld") }
                    .apply {
                        assertThat(this.prepareForTesting())
                                .isEqualTo(Message(
                                        "`HelloWorld`",
                                        ContentType.MARKDOWN,
                                        now.plusSeconds(1).truncatedTo(MILLIS),
                                        "test",
                                        "http://test.com"
                                ))
                    }
        }
    }
}
