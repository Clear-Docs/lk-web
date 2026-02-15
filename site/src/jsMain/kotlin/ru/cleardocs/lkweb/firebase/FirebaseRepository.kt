package ru.cleardocs.lkweb.firebase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AuthState {
    Loading,
    Authenticated,
    Unauthenticated,
}

class FirebaseRepository(
    private val firebase: FirebaseContext
) {
    private val _authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    private val _profileFlow = MutableStateFlow<FirebaseProfile?>(null)
    val profileFlow: StateFlow<FirebaseProfile?> = _profileFlow.asStateFlow()

    val auth: dynamic
        get() = firebase.auth

    private val unsubscribeAuthListener: () -> Unit =
        onAuthStateChanged(firebase.auth) { user ->
            _authStateFlow.value =
                if (user == null) AuthState.Unauthenticated else AuthState.Authenticated
            _profileFlow.value = firebaseUserProfile(user)
        }

    fun dispose() {
        unsubscribeAuthListener()
    }
}
