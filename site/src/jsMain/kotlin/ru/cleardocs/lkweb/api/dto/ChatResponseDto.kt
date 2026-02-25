package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

/** Ответ GET /api/v1/chat (chat-controller). Swagger: ChatResponseDto. */
@Serializable
data class ChatResponseDto(
    val apiKey: String,
    val personaId: Int,
)
