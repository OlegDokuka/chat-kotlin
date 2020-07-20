package com.example.kotlin.chat.repository

import com.example.kotlin.chat.repository.domain.Message
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param


interface MessageRepository : PagingAndSortingRepository<Message, String> {

    // language=SQL
    @Query("""
        SELECT * FROM (
            SELECT * FROM MESSAGES
            ORDER BY "SENT" DESC
            LIMIT 10
        ) ORDER BY "SENT"
    """)
    fun findLatest(): List<Message>

    // language=SQL
    @Query("""
        SELECT * FROM (
            SELECT * FROM MESSAGES
            WHERE SENT > (SELECT SENT FROM MESSAGES WHERE ID = :id)
            ORDER BY "SENT" DESC 
        ) ORDER BY "SENT"
    """)
    fun findLatest(@Param("id") id: String): List<Message>
}