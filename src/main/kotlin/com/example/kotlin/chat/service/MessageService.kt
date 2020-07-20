package com.example.kotlin.chat.service

import com.example.kotlin.chat.service.vm.MessageVM

interface MessageService {

    suspend fun latest(): List<MessageVM>

    suspend fun latestAfter(lastMessageId: String): List<MessageVM>

    suspend fun post(message: MessageVM)
}