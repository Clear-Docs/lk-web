package ru.cleardocs.lkweb.api

/**
 * События из стримингового ответа Onyx Chat API.
 */
sealed class StreamEvent {
    data class Content(val text: String) : StreamEvent()
    data class Citation(val citationNumber: Int, val documentId: String) : StreamEvent()
    data class Document(val documentId: String, val title: String?) : StreamEvent()
    /** Рассуждение модели (reasoning_delta). */
    data class Reasoning(val text: String) : StreamEvent()
}
