package ru.cleardocs.lkweb.profile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.cleardocs.lkweb.api.BackendApi
import ru.cleardocs.lkweb.api.BackendError
import ru.cleardocs.lkweb.api.dto.MeDto
import ru.cleardocs.lkweb.firebase.FirebaseProvider

/**
 * Состояние авторизации, определяемое по ответу бэкенда /api/v1/users/me.
 * Бэкенд валидирует Firebase-токен, поэтому 401/403 — фактическая «не авторизован».
 */
enum class ProfileAuthState {
    Loading,
    Authenticated,
    Unauthenticated,
}

class MeViewModel(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {

    val repository = FirebaseProvider.repository

    private val _me = MutableStateFlow<MeDto?>(null)
    val me: StateFlow<MeDto?> = _me.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Состояние авторизации по результату me() — 401/403 = Unauthenticated. */
    val authState: StateFlow<ProfileAuthState> = combine(_loading, _me, _error) { loading, me, error ->
        when {
            me != null -> ProfileAuthState.Authenticated
            isUnauthError(error) -> ProfileAuthState.Unauthenticated
            loading -> ProfileAuthState.Loading
            else -> ProfileAuthState.Unauthenticated
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), ProfileAuthState.Loading)

    init {
        scope.launch { loadMe() }
    }

    private fun isUnauthError(error: String?): Boolean {
        if (error == null) return false
        return error.contains("401") || error.contains("403") ||
            error.contains("Сессия истекла") || error.contains("Доступ запрещён") ||
            error.contains("User not authenticated", ignoreCase = true) ||
            error.contains("не авторизован", ignoreCase = true)
    }

    /**
     * Загружает данные текущего пользователя из REST GET /api/v1/users/me.
     * Токен и Firebase проверяются в BackendApi. 401/403 от бэка = не авторизован.
     */
    suspend fun loadMe() {
        _loading.value = true
        _error.value = null
        try {
            val response = BackendApi.me()
            _me.value = response
        } catch (e: Throwable) {
            val errorMsg = when {
                e is BackendError -> when (e.code) {
                    401 -> "Сессия истекла"
                    403 -> "Доступ запрещён. Возможно, аккаунт ещё не зарегистрирован в системе."
                    else -> e.message ?: "Ошибка ${e.code}"
                }
                e.message?.contains("401") == true -> "Сессия истекла"
                e.message?.contains("403") == true -> "Доступ запрещён. Возможно, аккаунт ещё не зарегистрирован в системе."
                e.message?.contains("Backend unreachable") == true -> "Сервер недоступен. Проверьте подключение к сети."
                e.message?.contains("unreachable") == true -> "Сервер недоступен"
                e.message?.contains("User not authenticated") == true -> "Пользователь не авторизован"
                else -> e.message ?: "Ошибка загрузки профиля"
            }
            _error.value = errorMsg
            _me.value = null
        } finally {
            _loading.value = false
        }
    }
}
