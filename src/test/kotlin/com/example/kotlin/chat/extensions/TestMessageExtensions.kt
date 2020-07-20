package com.example.kotlin.chat.extensions

import com.example.kotlin.chat.repository.domain.Message
import com.example.kotlin.chat.service.vm.MessageVM
import java.time.temporal.ChronoUnit.MILLIS


fun MessageVM.prepareForTesting() = copy(id = null, sent = sent.truncatedTo(MILLIS))

fun Message.prepareForTesting() = copy(id = null, sent = sent.truncatedTo(MILLIS))