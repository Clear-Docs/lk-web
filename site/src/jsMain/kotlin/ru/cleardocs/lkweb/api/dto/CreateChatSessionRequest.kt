package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateChatSessionRequest(
    @SerialName("persona_id") val personaId: Int,
    val description: String = "Onyx Widget Session",
)
