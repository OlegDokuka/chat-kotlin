package com.example.kotlin.chat.controller

import com.example.kotlin.chat.service.MessageService
import com.example.kotlin.chat.service.vm.MessageVM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Suppress("EXPERIMENTAL_API_USAGE")
@RestController
@RequestMapping("/api/v1/messages")
class MessageResource(val messageService: MessageService) {

    @GetMapping(produces = [TEXT_EVENT_STREAM_VALUE])
    fun stream(@RequestParam(value = "lastMessageId", defaultValue = "") lastMessageId: String): Flow<MessageVM> =
            messageService.stream()
                    .onStart {
                        emitAll(
                                if (lastMessageId.isNotEmpty()) {
                                    messageService.latestAfter(lastMessageId)
                                } else {
                                    messageService.latest()
                                }
                        )
                    }

    @PostMapping
    suspend fun post(@RequestBody message: MessageVM) {
        messageService.post(message)
    }
}