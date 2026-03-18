package ru.cleardocs.lkweb.chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import ru.cleardocs.lkweb.api.ChatApi

/**
 * ViewModel чата со стриминговой подпиской через Flow.
 * Использует [ChatApi.createChatSession] и [ChatApi.sendMessageStream].
 */
class ChatViewModel(
    private val personaId: Int = 0,
    private val apiKey: String? = null,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _chatSessionId = MutableStateFlow<String?>(null)
    val chatSessionId: StateFlow<String?> = _chatSessionId.asStateFlow()

    private var sendJob: Job? = null

    /** Таймаут ожидания ответа от сервера (мс). По истечении показываем ошибку вместо бесконечной загрузки. */
    private val responseTimeoutMs = 120_000L

    private fun userFriendlyError(e: Throwable): String = when {
        e is TimeoutCancellationException -> "Превышено время ожидания ответа. Проверьте работу сервера или попробуйте снова."
        e.message?.lowercase()?.contains("network") == true ||
            e.message?.lowercase()?.contains("failed to fetch") == true ->
            "Нет связи с сервером. Проверьте интернет и доступность сервиса."
        else -> e.message ?: "Неизвестная ошибка"
    }

    /**
     * Отправляет сообщение и подписывается на стрим ответа.
     */
    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || _loading.value) return

        sendJob?.cancel()
        _error.value = null
        _loading.value = true

        _messages.update { it + ChatMessage(ChatRole.USER, trimmed) }
        _messages.update { it + ChatMessage(ChatRole.ASSISTANT, "", isLoading = true) }

        sendJob = scope.launch {
            var fullText = ""
            var reasoningText = ""
            val citationToDocId = mutableMapOf<Int, String>()
            val docIdToTitle = mutableMapOf<String, String>()
            try {
                var sessionId = _chatSessionId.value
                if (sessionId == null) {
                    sessionId = ChatApi.createChatSession(personaId = personaId, apiKey = apiKey)
                    _chatSessionId.value = sessionId
                }

                val flow = ChatApi.sendMessageStream(
                    message = trimmed,
                    chatSessionId = sessionId,
                    personaId = personaId,
                    apiKey = apiKey,
                )

                withTimeout(responseTimeoutMs) {
                    flow.collect { event ->
                        when (event) {
                            is ru.cleardocs.lkweb.api.StreamEvent.Content -> {
                                fullText += event.text
                                _messages.update { list ->
                                    val last = list.lastOrNull()
                                    if (last?.role == ChatRole.ASSISTANT && last.isLoading) {
                                        list.dropLast(1) + last.copy(content = fullText)
                                    } else list
                                }
                            }
                            is ru.cleardocs.lkweb.api.StreamEvent.Reasoning -> {
                                reasoningText += event.text
                                _messages.update { list ->
                                    val last = list.lastOrNull()
                                    if (last?.role == ChatRole.ASSISTANT && last.isLoading) {
                                        list.dropLast(1) + last.copy(reasoning = reasoningText)
                                    } else list
                                }
                            }
                            is ru.cleardocs.lkweb.api.StreamEvent.Citation -> {
                                citationToDocId[event.citationNumber] = event.documentId
                            }
                            is ru.cleardocs.lkweb.api.StreamEvent.Document -> {
                                event.title?.let { docIdToTitle[event.documentId] = it }
                            }
                        }
                    }
                }

                val citations = citationToDocId.mapValues { (_, docId) ->
                    docIdToTitle[docId] ?: "Источник"
                }

                _messages.update { list ->
                    val last = list.lastOrNull()
                    if (last?.role == ChatRole.ASSISTANT) {
                        list.dropLast(1) + last.copy(
                            content = fullText.ifBlank { "(Пустой ответ)" },
                            isLoading = false,
                            citations = citations,
                            citationDocumentIds = citationToDocId,
                            reasoning = reasoningText,
                        )
                    } else list
                }
            } catch (e: Throwable) {
                val msg = userFriendlyError(e)
                val isNetworkBreak = e.message?.lowercase()?.let {
                    it.contains("network") || it.contains("failed to fetch")
                } ?: false
                _messages.update { list ->
                    val last = list.lastOrNull()
                    if (last?.role == ChatRole.ASSISTANT && last.isLoading) {
                        val hasPartial = fullText.isNotBlank() || reasoningText.isNotBlank()
                        val content = when {
                            hasPartial && isNetworkBreak ->
                                fullText.ifBlank { "(нет текста)" } + "\n\nСоединение прервано. Ответ может быть неполным."
                            else -> "Ошибка: $msg"
                        }
                        val citations = citationToDocId.mapValues { (_, docId) ->
                            docIdToTitle[docId] ?: "Источник"
                        }
                        list.dropLast(1) + last.copy(
                            content = content,
                            isLoading = false,
                            reasoning = reasoningText,
                            citations = citations,
                            citationDocumentIds = citationToDocId,
                        )
                    } else list
                }
                _error.value = if (fullText.isNotBlank() || reasoningText.isNotBlank()) {
                    "Соединение прервано. Ответ может быть неполным."
                } else msg
            } finally {
                _loading.value = false
            }
        }
    }

    /** Показать сообщение об ошибке (например, при загрузке файла). */
    fun showError(message: String) {
        _error.value = message
    }

    /**
     * Сброс сессии и истории.
     */
    fun resetConversation() {
        sendJob?.cancel()
        _chatSessionId.value = null
        _messages.value = emptyList()
        _error.value = null
        _loading.value = false
    }
}
