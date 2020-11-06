package com.example.kotlin.chat.service.impl

import com.example.kotlin.chat.repository.MessageRepository
import com.example.kotlin.chat.repository.domain.ContentType
import com.example.kotlin.chat.repository.domain.Message
import com.example.kotlin.chat.service.MessageService
import com.example.kotlin.chat.service.vm.MessageVM
import com.example.kotlin.chat.service.vm.UserVM
import org.springframework.stereotype.Service
import java.net.URL

@Service
class PersistentMessageService(val messageRepository: MessageRepository) : MessageService {

    override fun latest(): List<MessageVM> =
            messageRepository.findLatest()
                    .map { with(it) { MessageVM(content, UserVM(username, URL(userAvatarImageLink)), sent, id) } }

    override fun latestAfter(lastMessageId: String): List<MessageVM> =
            messageRepository.findLatest(lastMessageId)
                    .map { with(it) { MessageVM(content, UserVM(username, URL(userAvatarImageLink)), sent, id) } }

    override fun post(message: MessageVM) {
        messageRepository.save(
                with(message) { Message(content, ContentType.PLAIN, sent, user.name, user.avatarImageLink.toString()) }
        )
    }
}