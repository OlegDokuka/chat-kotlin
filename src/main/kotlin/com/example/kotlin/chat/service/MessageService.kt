package com.example.kotlin.chat.service

import com.example.kotlin.chat.service.vm.MessageVM
import kotlinx.coroutines.flow.Flow

interface MessageService {

    fun latest(): Flow<MessageVM>

    fun latestAfter(lastMessageId: String): Flow<MessageVM>

    fun stream(): Flow<MessageVM>

    suspend fun post(messages: Flow<MessageVM>)
}