package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateChatSessionResponse(
    @SerialName("chat_session_id") val chatSessionId: String,
)
