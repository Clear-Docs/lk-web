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
import ru.cleardocs.lkweb.api.dto.MeDto
import ru.cleardocs.lkweb.utils.isUnauthError
import ru.cleardocs.lkweb.utils.toUserFriendlyMessage
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
            error.isUnauthError() -> ProfileAuthState.Unauthenticated
            loading -> ProfileAuthState.Loading
            else -> ProfileAuthState.Unauthenticated
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), ProfileAuthState.Loading)

    init {
        scope.launch { loadMe() }
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
            _error.value = e.toUserFriendlyMessage("Ошибка загрузки профиля")
            _me.value = null
        } finally {
            _loading.value = false
        }
    }
}
