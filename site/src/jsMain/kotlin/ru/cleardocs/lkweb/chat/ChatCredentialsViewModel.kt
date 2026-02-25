package ru.cleardocs.lkweb.chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.cleardocs.lkweb.api.ChatService
import ru.cleardocs.lkweb.api.dto.ChatResponseDto

/**
 * ViewModel для загрузки credentials чата (apiKey, personaId) с бэкенда.
 * В init вызывает [ChatService.chat].
 */
class ChatCredentialsViewModel(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {

    private val _credentials = MutableStateFlow<ChatResponseDto?>(null)
    val credentials: StateFlow<ChatResponseDto?> = _credentials.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        scope.launch {
            try {
                _credentials.value = ChatService.chat()
            } catch (e: Throwable) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
