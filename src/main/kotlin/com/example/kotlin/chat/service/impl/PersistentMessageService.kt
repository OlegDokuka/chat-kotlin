package com.example.kotlin.chat.service.impl

import com.example.kotlin.chat.extensions.asDomainObject
import com.example.kotlin.chat.extensions.mapToViewModel
import com.example.kotlin.chat.repository.MessageRepository
import com.example.kotlin.chat.service.MessageService
import com.example.kotlin.chat.service.vm.MessageVM
import org.springframework.stereotype.Service

@Service
class PersistentMessageService(val messageRepository: MessageRepository) : MessageService {

    override suspend fun latest(): List<MessageVM> =
            messageRepository.findLatest()
                .mapToViewModel()

    override suspend fun latestAfter(lastMessageId: String): List<MessageVM> =
            messageRepository.findLatest(lastMessageId)
                .mapToViewModel()

    override suspend fun post(message: MessageVM) {
        messageRepository.save(message.asDomainObject())
    }
}