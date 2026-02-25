package ru.cleardocs.lkweb.api

import ru.cleardocs.lkweb.api.dto.ChatResponseDto

/**
 * Сервис для работы с чатом.
 * Получает credentials (apiKey, personaId) с бэкенда для использования в ChatBlock.
 */
object ChatService {
    suspend fun chat(): ChatResponseDto = BackendApi.chat()
}
