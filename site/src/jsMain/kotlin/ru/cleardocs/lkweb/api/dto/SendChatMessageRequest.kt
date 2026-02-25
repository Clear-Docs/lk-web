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

@Serializable
data class InternalSearchFilters(
    @SerialName("document_set") val documentSet: String? = null,
    @SerialName("source_type") val sourceType: List<String> = emptyList(),
)

/** POST /chat/send-chat-message */
@Serializable
data class SendChatMessageRequest(
    val message: String,
    @SerialName("chat_session_id") val chatSessionId: String? = null,
    @SerialName("chat_session_info") val chatSessionInfo: ChatSessionInfo? = null,
    @SerialName("parent_message_id") val parentMessageId: Int? = null,
    @SerialName("file_descriptors") val fileDescriptors: List<FileDescriptorRef> = emptyList(),
    @SerialName("search_doc_ids") val searchDocIds: List<String> = emptyList(),
    @SerialName("internal_search_filters") val internalSearchFilters: InternalSearchFilters? = null,
    @SerialName("allowed_tool_ids") val allowedToolIds: List<Int>? = null,
    @SerialName("forced_tool_id") val forcedToolId: Int? = null,
    @SerialName("stream") val stream: Boolean = true,
)
