package com.example.kotlin.chat.extensions

import com.example.kotlin.chat.repository.domain.ContentType
import com.example.kotlin.chat.repository.domain.Message
import com.example.kotlin.chat.service.vm.MessageVM
import com.example.kotlin.chat.service.vm.UserVM
import java.net.URL


fun MessageVM.asDomainObject(contentType: ContentType = ContentType.PLAIN): Message = Message(
        content,
        contentType,
        sent,
        user.name,
        user.avatarImageLink.toString(),
        id
)

fun Message.asViewModel(): MessageVM = MessageVM(
        content,
        UserVM(username, URL(userAvatarImageLink)),
        sent,
        id
)

fun List<Message>.mapToViewModel(): List<MessageVM> = map { it.asViewModel() }