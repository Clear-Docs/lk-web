package ru.cleardocs.lkweb.chat

/**
 * Сообщение в чате.
 * @param citations Карта номер цитаты (1, 2, …) → заголовок источника (metadata.Title)
 */
data class ChatMessage(
    val role: ChatRole,
    val content: String,
    val isLoading: Boolean = false,
    val citations: Map<Int, String> = emptyMap(),
)

enum class ChatRole {
    USER,
    ASSISTANT,
}
