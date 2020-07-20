package com.example.kotlin.chat.controller

import com.example.kotlin.chat.service.MessageService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HtmlController(val messageService: MessageService) {

    @GetMapping("/")
    suspend fun index(): String {
        return "chat"
    }

}