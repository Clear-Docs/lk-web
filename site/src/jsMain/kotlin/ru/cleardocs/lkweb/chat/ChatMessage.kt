package ru.cleardocs.lkweb.chat

/**
 * Сообщение в чате.
 * @param citations Карта номер цитаты (1, 2, …) → заголовок источника (metadata.Title)
 * @param citationDocumentIds Карта номер цитаты → documentId для загрузки файла по Onyx API
 */
data class ChatMessage(
    val role: ChatRole,
    val content: String,
    val isLoading: Boolean = false,
    val citations: Map<Int, String> = emptyMap(),
    val citationDocumentIds: Map<Int, String> = emptyMap(),
)

enum class ChatRole {
    USER,
    ASSISTANT,
}
