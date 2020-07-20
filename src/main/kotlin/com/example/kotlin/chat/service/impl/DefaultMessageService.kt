package com.example.kotlin.chat.service.impl

import com.example.kotlin.chat.extensions.asDomainObject
import com.example.kotlin.chat.extensions.asViewModel
import com.example.kotlin.chat.repository.MessageRepository
import com.example.kotlin.chat.service.MessageService
import com.example.kotlin.chat.service.vm.MessageVM
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class DefaultMessageService(val messageRepository: MessageRepository) : MessageService {

    override suspend fun latest(): List<MessageVM> =
            messageRepository.findLatest()
                    .map { it.asViewModel() }
                    .toList()

    override suspend fun latestAfter(lastMessageId: String): List<MessageVM> =
            messageRepository.findLatest(lastMessageId)
                    .map { it.asViewModel() }
                    .toList()

    override suspend fun post(message: MessageVM) {
        messageRepository.save(message.asDomainObject())
    }
}