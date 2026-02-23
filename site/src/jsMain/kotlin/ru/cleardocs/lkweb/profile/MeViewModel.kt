package ru.cleardocs.lkweb.profile

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.cleardocs.lkweb.api.BackendApi
import ru.cleardocs.lkweb.api.dto.MeDto
import ru.cleardocs.lkweb.firebase.getIdToken

class MeViewModel {

    private val _me = MutableStateFlow<MeDto?>(null)
    val me: StateFlow<MeDto?> = _me.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Загружает данные текущего пользователя из REST GET /api/v1/users/me.
     * Требуется Firebase user для получения idToken.
     */
    suspend fun loadMe(user: dynamic) {
        _loading.value = true
        _error.value = null
        try {
            val token = getIdToken(user)
            val response = BackendApi.me(token)
            _me.value = response
        } catch (e: Throwable) {
            val errorMsg = when {
                e is ru.cleardocs.lkweb.api.BackendError -> e.message ?: "Ошибка ${e.code}"
                e.message?.contains("401") == true -> "Сессия истекла"
                e.message?.contains("403") == true -> "Доступ запрещён. Возможно, аккаунт ещё не зарегистрирован в системе."
                e.message?.contains("Backend unreachable") == true -> "Сервер недоступен. Проверьте подключение к сети."
                e.message?.contains("unreachable") == true -> "Сервер недоступен"
                else -> e.message ?: "Ошибка загрузки профиля"
            }
            _error.value = errorMsg
            _me.value = null
        } finally {
            _loading.value = false
        }
    }
}
