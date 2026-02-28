package ru.cleardocs.lkweb.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.cleardocs.lkweb.api.BackendApi
import ru.cleardocs.lkweb.firebase.FirebaseProvider
import ru.cleardocs.lkweb.firebase.AuthState
import ru.cleardocs.lkweb.firebase.createUserWithEmailAndPassword
import ru.cleardocs.lkweb.firebase.sendEmailVerification
import ru.cleardocs.lkweb.firebase.signInWithEmailAndPassword
import ru.cleardocs.lkweb.firebase.signInWithGoogle

enum class AuthFormMode {
    SIGN_IN,
    SIGN_UP
}

data class AuthUiState(
    val mode: AuthFormMode = AuthFormMode.SIGN_IN,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val navigateToProfile: Boolean = false,
)

class AuthViewModel(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    private val repository = FirebaseProvider.repository

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        scope.launch {
            repository.authStateFlow.collect { authState ->
                if (authState == AuthState.Authenticated && !_state.value.isLoading) {
                    _state.value = _state.value.copy(
                        errorMessage = null,
                        navigateToProfile = true,
                    )
                }
            }
        }
    }

    fun setMode(mode: AuthFormMode) {
        _state.value = _state.value.copy(
            mode = mode,
            errorMessage = null,
            successMessage = null,
        )
    }

    fun setEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun setPassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun setConfirmPassword(confirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = confirmPassword)
    }

    fun setPasswordVisible(visible: Boolean) {
        _state.value = _state.value.copy(isPasswordVisible = visible)
    }

    fun setConfirmPasswordVisible(visible: Boolean) {
        _state.value = _state.value.copy(isConfirmPasswordVisible = visible)
    }

    fun clearNavigateToProfile() {
        _state.value = _state.value.copy(navigateToProfile = false)
    }

    fun signInWithEmail() {
        val s = _state.value
        val normalizedEmail = s.email.trim()
        if (normalizedEmail.isEmpty() || s.password.isEmpty()) {
            _state.value = s.copy(errorMessage = "Заполните email и пароль.")
            return
        }

        scope.launch {
            _state.value = s.copy(
                errorMessage = null,
                successMessage = null,
                isLoading = true,
            )
            try {
                signInWithEmailAndPassword(repository.auth, normalizedEmail, s.password)
                console.log("[Auth] Вход успешен")
                _state.value = _state.value.copy(
                    isLoading = false,
                    navigateToProfile = true,
                )
            } catch (e: dynamic) {
                console.error("[Auth] Ошибка:", e?.code, e?.message, e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = authErrorToMessage(e, isGoogleAuth = false),
                )
            }
        }
    }

    fun signUpWithEmail() {
        val s = _state.value
        val normalizedEmail = s.email.trim()
        if (normalizedEmail.isEmpty() || s.password.isEmpty()) {
            _state.value = s.copy(errorMessage = "Заполните email и пароль.")
            return
        }
        if (s.password.length < 6) {
            _state.value = s.copy(errorMessage = "Пароль должен содержать минимум 6 символов.")
            return
        }
        if (s.password != s.confirmPassword) {
            _state.value = s.copy(errorMessage = "Пароли не совпадают.")
            return
        }

        scope.launch {
            _state.value = s.copy(
                errorMessage = null,
                successMessage = null,
                isLoading = true,
            )
            try {
                val result = createUserWithEmailAndPassword(
                    repository.auth,
                    normalizedEmail,
                    s.password,
                )
                val user = result?.user
                if (user != null) {
                    try {
                        sendEmailVerification(user)
                    } catch (verifyErr: dynamic) {
                        console.error("[Auth] sendEmailVerification ошибка", verifyErr)
                    }
                    BackendApi.register()
                }
                console.log("[Auth] Регистрация: завершено")
                _state.value = _state.value.copy(
                    isLoading = false,
                    navigateToProfile = true,
                )
            } catch (e: dynamic) {
                console.error("[Auth] Ошибка:", e?.code, e?.message, e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = authErrorToMessage(e, isGoogleAuth = false),
                )
            }
        }
    }

    fun signInWithGoogle() {
        val s = _state.value
        scope.launch {
            _state.value = s.copy(
                errorMessage = null,
                isLoading = true,
            )
            try {
                signInWithGoogle(repository.auth)
                BackendApi.register()
                _state.value = _state.value.copy(
                    isLoading = false,
                    navigateToProfile = true,
                )
            } catch (e: dynamic) {
                console.error("Google sign-in failed", e?.code, e?.message, e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = authErrorToMessage(e, isGoogleAuth = true),
                )
            }
        }
    }
}

private fun authErrorToMessage(error: dynamic, isGoogleAuth: Boolean = false): String {
    val code = error?.code as? String
    return when (code) {
        "auth/invalid-email" -> "Некорректный email."
        "auth/invalid-credential" -> {
            if (isGoogleAuth) {
                "Google-вход отклонен. Проверьте настройки Google-провайдера в Firebase."
            } else {
                "Неверный email или пароль."
            }
        }
        "auth/user-not-found" -> "Пользователь не найден."
        "auth/wrong-password" -> "Неверный пароль."
        "auth/email-already-in-use" -> "Этот email уже используется."
        "auth/weak-password" -> "Слишком простой пароль."
        "auth/operation-not-allowed" -> "Google-вход не включен в Firebase (Authentication -> Sign-in method)."
        "auth/too-many-requests" -> "Слишком много попыток. Попробуйте позже."
        "auth/unauthorized-domain" -> "Текущий домен не добавлен в Authorized domains в Firebase."
        "auth/popup-closed-by-user" -> "Вход через Google отменен."
        "auth/popup-blocked" -> "Браузер заблокировал всплывающее окно для Google-входа."
        else -> {
            if (isGoogleAuth) {
                "Не удалось войти через Google. Код: ${code ?: "unknown"}."
            } else {
                "Не удалось выполнить авторизацию. Попробуйте еще раз."
            }
        }
    }
}
