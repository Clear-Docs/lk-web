package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileDescriptorRef(
    val id: String? = null,
)

@Serializable
data class ChatSessionInfo(
    @SerialName("persona_id") val personaId: Int,
    @SerialName("project_id") val projectId: String? = null,
)

/**
 * POST /chat/send-chat-message (Onyx API, Swagger: /api/docs).
 * @see https://docs.onyx.app/developers/guides/chat_new_guide
 */
@Serializable
data class SendChatMessageRequest(
    val message: String,
    @SerialName("chat_session_id") val chatSessionId: String? = null,
    @SerialName("chat_session_info") val chatSessionInfo: ChatSessionInfo? = null,
    @SerialName("parent_message_id") val parentMessageId: Int? = null,
    @SerialName("file_descriptors") val fileDescriptors: List<FileDescriptorRef> = emptyList(),
    @SerialName("search_doc_ids") val searchDocIds: List<String> = emptyList(),
    @SerialName("stream") val stream: Boolean = true,
)
