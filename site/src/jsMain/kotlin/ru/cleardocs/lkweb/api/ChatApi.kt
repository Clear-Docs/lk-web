package ru.cleardocs.lkweb.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.js.json
import kotlinx.browser.window
import ru.cleardocs.lkweb.ApiConfig
import ru.cleardocs.lkweb.api.dto.CreateChatSessionRequest
import ru.cleardocs.lkweb.api.dto.CreateChatSessionResponse
import ru.cleardocs.lkweb.api.dto.SendChatMessageRequest

/**
 * API-клиент для чата. createChatSession — ClearDocs, sendMessageStream — Onyx.
 */
object ChatApi {

    private val client = ApiConfig.createHttpClient()
    private val onyxClient = ApiConfig.createOnyxHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Создаёт сессию чата.
     * POST /api/v1/chat/create-chat-session
     */
    suspend fun createChatSession(
        personaId: Int,
        description: String = "Widget Session",
        apiKey: String? = null,
    ): String {
        val req = CreateChatSessionRequest(personaId = personaId, description = description)
        val res = client.post("api/v1/chat/create-chat-session") {
            contentType(ContentType.Application.Json)
            setBody(req)
            apiKey?.let { header("Authorization", "Bearer $it") }
        }
        val data: CreateChatSessionResponse = res.body()
        return data.chatSessionId
    }

    /**
     * Отправляет сообщение с потоковым ответом.
     * POST /api/chat/send-chat-message (Onyx)
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
            internalSearchFilters = ru.cleardocs.lkweb.api.dto.InternalSearchFilters(
                sourceType = listOf("file", "web")
            ),
            allowedToolIds = listOf(1), // todo get tool смотри там приходят тулсы
            forcedToolId = 1,
            stream = true,
        )
        val body = json.encodeToString(ru.cleardocs.lkweb.api.dto.SendChatMessageRequest.serializer(), req)
        val headers = mutableMapOf<String, String>("Content-Type" to "application/json")
        apiKey?.let { headers["Authorization"] = "Bearer $it" }
        return fetchStream(
            url = "${ApiConfig.onyxBaseUrl}/api/chat/send-chat-message",
            headers = headers,
            body = body,
        ).flatMapConcat { line ->
            flowOf(*parseStreamLine(line).toTypedArray())
        }
    }

    /** Извлекает UUID из document_id формата FILE_CONNECTOR__{uuid}. */
    private val uuidInDocumentId = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")

    /**
     * Ссылка для прямого открытия файла (как в Onyx).
     * localhost: прокси 9081 поддерживает ?token= — добавляет Authorization при проксировании.
     * production: api.cleardocs.ru — авторизация через cookie/сессию.
     */
    fun fileUrl(documentId: String, apiKey: String?): String {
        val fileId = uuidInDocumentId.find(documentId)?.value ?: documentId
        val base = "${ApiConfig.onyxBaseUrl}/api/chat/file/$fileId"
        return if (apiKey != null) "$base?token=${js("encodeURIComponent")(apiKey).unsafeCast<String>()}" else base
    }

    /**
     * Загружает байты файла по Onyx API. GET /api/chat/file/{documentId}
     * document_id из потока: "FILE_CONNECTOR__{uuid}" — endpoint ожидает только UUID.
     */
    private suspend fun fetchFileBytes(documentId: String, apiKey: String?): Pair<ByteArray, String> {
        val fileId = uuidInDocumentId.find(documentId)?.value ?: documentId
        val response = onyxClient.get("api/chat/file/$fileId") {
            header("Accept", "*/*")
            apiKey?.let { header("Authorization", "Bearer $it") }
        }
        if (!response.status.isSuccess()) {
            throw Exception("Файл не загружен: ${response.status}")
        }
        val bytes: ByteArray = response.body()
        val contentType = response.contentType()?.toString() ?: "application/octet-stream"
        return bytes to contentType
    }

    /**
     * Загружает файл по Onyx API.
     * @return Data URL для отображения в iframe/embed (PDF, изображения и др.)
     */
    suspend fun fetchFile(documentId: String, apiKey: String?): String {
        val (bytes, contentType) = fetchFileBytes(documentId, apiKey)
        val base64 = bytesToBase64(bytes)
        return "data:$contentType;base64,$base64"
    }

    /**
     * Создаёт blob URL из ByteArray через чистый Kotlin/JS.
     * js("new Uint8Array") выдаёт "(intermediate value) is not a function" — используем wrapper.
     */
    private fun bytesToBlobUrl(bytes: ByteArray, contentType: String): String {
        val createUrl = js(
            """
            (function(bytes, type) {
                var len = bytes.length;
                var arr = new Uint8Array(len);
                for (var i = 0; i < len; i++) arr[i] = bytes[i] & 0xFF;
                var blob = new Blob([arr], { type: type });
                return URL.createObjectURL(blob);
            })
            """
        ).unsafeCast<(ByteArray, String) -> String>()
        return createUrl(bytes, contentType)
    }

    /**
     * Загружает файл и открывает в переданном окне (открытом синхронно при клике).
     * Браузер блокирует window.open после async — окно должно быть открыто заранее.
     */
    suspend fun openFileInWindow(
        documentId: String,
        displayName: String,
        apiKey: String?,
        targetWindow: dynamic,
    ) {
        val (bytes, contentType) = fetchFileBytes(documentId, apiKey)
        val blobUrl = bytesToBlobUrl(bytes, contentType)
        val w = targetWindow
        if (w != null && jsTypeOf(w.location) != "undefined") {
            w.location.href = blobUrl
        }
        window.setTimeout(js("(function(url){ return function(){ URL.revokeObjectURL(url); }; })")(blobUrl), 5000)
    }

    private fun bytesToBase64(bytes: ByteArray): String {
        val chunkSize = 8192
        val sb = StringBuilder()
        for (i in bytes.indices step chunkSize) {
            val end = minOf(i + chunkSize, bytes.size)
            for (j in i until end) {
                sb.append(bytes[j].toInt().and(0xFF).toChar())
            }
        }
        return js("btoa")(sb.toString()).unsafeCast<String>()
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
                "reasoning_delta" -> {
                    val reasoning = packet["reasoning"]?.jsonPrimitive?.content
                    if (reasoning != null) listOf(StreamEvent.Reasoning(reasoning)) else emptyList()
                }
                else -> emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
