package com.example.kotlin.chat.extensions

import com.example.kotlin.chat.repository.domain.ContentType
import com.example.kotlin.chat.repository.domain.Message
import com.example.kotlin.chat.service.vm.MessageVM
import com.example.kotlin.chat.service.vm.UserVM
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.net.URL


fun MessageVM.asDomainObject(contentType: ContentType = ContentType.MARKDOWN): Message = Message(
        content,
        contentType,
        sent,
        user.name,
        user.avatarImageLink.toString(),
        id
)

fun Message.asViewModel(): MessageVM = MessageVM(
        contentType.render(content),
        UserVM(username, URL(userAvatarImageLink)),
        sent,
        id
)

fun ContentType.render(content: String): String = when (this) {
    ContentType.PLAIN -> content
    ContentType.MARKDOWN -> {
        val flavour = CommonMarkFlavourDescriptor()
        HtmlGenerator(content, MarkdownParser(flavour).buildMarkdownTreeFromString(content), flavour).generateHtml()
    }
}