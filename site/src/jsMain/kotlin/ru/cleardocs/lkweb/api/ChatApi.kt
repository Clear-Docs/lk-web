package ru.cleardocs.lkweb.api

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ru.cleardocs.lkweb.ApiConfig
import ru.cleardocs.lkweb.api.dto.CreateChatSessionRequest
import ru.cleardocs.lkweb.api.dto.CreateChatSessionResponse
import ru.cleardocs.lkweb.api.dto.SendChatMessageRequest

/**
 * API-клиент для чата. Использует [ApiConfig.createHttpClient] и [ApiConfig.baseUrl].
 */
object ChatApi {

    private val client = ApiConfig.createHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Создаёт сессию чата.
     * POST /chat/create-session
     */
    suspend fun createChatSession(
        personaId: Int,
        description: String = "Widget Session",
        apiKey: String? = null,
    ): String {
        val req = CreateChatSessionRequest(personaId = personaId, description = description)
        val res = client.post("chat/create-session") {
            contentType(ContentType.Application.Json)
            setBody(req)
            apiKey?.let { header("Authorization", "Bearer $it") }
        }
        val data: CreateChatSessionResponse = res.body()
        return data.chatSessionId
    }

    /**
     * Отправляет сообщение с потоковым ответом.
     * POST /chat/send-chat-message
     *
     * @return Flow событий: Content (текст), Citation, Document (метаданные источников)
     */
    fun sendMessageStream(
        message: String,
        chatSessionId: String?,
        personaId: Int,
        apiKey: String? = null,
        parentMessageId: Int? = null,
    ): Flow<StreamEvent> {
        val req = SendChatMessageRequest(
            message = message,
            chatSessionId = chatSessionId,
            chatSessionInfo = if (chatSessionId == null) ru.cleardocs.lkweb.api.dto.ChatSessionInfo(personaId = personaId) else null,
            parentMessageId = parentMessageId,
            internalSearchFilters = ru.cleardocs.lkweb.api.dto.InternalSearchFilters(sourceType = listOf("file")),
            allowedToolIds = listOf(1), // todo get tool смотри там приходят тулсы
            forcedToolId = 1,
            stream = true,
        )
        val body = json.encodeToString(ru.cleardocs.lkweb.api.dto.SendChatMessageRequest.serializer(), req)
        val headers = mutableMapOf<String, String>("Content-Type" to "application/json")
        apiKey?.let { headers["Authorization"] = "Bearer $it" }
        return fetchStream(
            url = "${ApiConfig.baseUrl}/chat/send-chat-message",
            headers = headers,
            body = body,
        ).flatMapConcat { line ->
            flowOf(*parseStreamLine(line).toTypedArray())
        }
    }

    private fun parseStreamLine(line: String): List<StreamEvent> {
        return try {
            val obj = json.parseToJsonElement(line).jsonObject
            val packet = obj["obj"]?.jsonObject ?: obj
            val type = packet["type"]?.jsonPrimitive?.content ?: return emptyList()
            when (type) {
                "message_delta" -> {
                    val content = packet["content"]?.jsonPrimitive?.content
                    if (content != null) listOf(StreamEvent.Content(content)) else emptyList()
                }
                "citation_info" -> {
                    val num = packet["citation_number"]?.jsonPrimitive?.content?.toIntOrNull()
                    val docId = packet["document_id"]?.jsonPrimitive?.content
                    if (num != null && docId != null) listOf(StreamEvent.Citation(num, docId)) else emptyList()
                }
                "search_tool_documents_delta" -> {
                    val docs = packet["documents"]?.jsonArray ?: return emptyList()
                    docs.mapNotNull { el ->
                        val doc = el.jsonObject
                        val docId = doc["document_id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                        val title = doc["metadata"]?.jsonObject?.get("Title")?.jsonPrimitive?.content
                            ?: doc["semantic_identifier"]?.jsonPrimitive?.content
                        StreamEvent.Document(docId, title)
                    }
                }
                "message_start" -> {
                    val docs = packet["final_documents"]?.jsonArray ?: return emptyList()
                    docs.mapNotNull { el ->
                        val doc = el.jsonObject
                        val docId = doc["document_id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                        val title = doc["metadata"]?.jsonObject?.get("Title")?.jsonPrimitive?.content
                            ?: doc["semantic_identifier"]?.jsonPrimitive?.content
                        StreamEvent.Document(docId, title)
                    }
                }
                else -> emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
