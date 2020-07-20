package com.example.kotlin.chat.service.impl

import com.example.kotlin.chat.extensions.asDomainObject
import com.example.kotlin.chat.extensions.asRendered
import com.example.kotlin.chat.extensions.asViewModel
import com.example.kotlin.chat.repository.MessageRepository
import com.example.kotlin.chat.service.MessageService
import com.example.kotlin.chat.service.vm.MessageVM
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import org.springframework.stereotype.Service

@Service
@Suppress("EXPERIMENTAL_API_USAGE")
class DefaultMessageService(val messageRepository: MessageRepository) : MessageService {

    val sender: BroadcastChannel<MessageVM> = BroadcastChannel(Channel.BUFFERED)

    override fun latest(): Flow<MessageVM> =
            messageRepository.findLatest()
                    .map { it.asViewModel() }

    override fun latestAfter(lastMessageId: String): Flow<MessageVM> =
            messageRepository.findLatest(lastMessageId)
                    .map { it.asViewModel() }

    override fun stream(): Flow<MessageVM> = sender.openSubscription().receiveAsFlow()

    override suspend fun post(message: MessageVM) {
        messageRepository.save(message.asDomainObject())
        sender.send(message.asRendered())
    }
}