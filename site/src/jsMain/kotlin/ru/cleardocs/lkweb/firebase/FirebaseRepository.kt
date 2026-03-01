package ru.cleardocs.lkweb.firebase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    val auth: dynamic
        get() = firebase.auth

    init {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                authStateReady(firebase.auth)
                val user = firebase.auth.currentUser
                _authStateFlow.value =
                    if (user != null) AuthState.Authenticated else AuthState.Unauthenticated
                onAuthStateChanged(firebase.auth) { u ->
                    _authStateFlow.value =
                        if (u == null) AuthState.Unauthenticated else AuthState.Authenticated
                }
            } catch (e: Throwable) {
                console.error("[FirebaseRepository.init] authStateReady failed", e)
                _authStateFlow.value =
                    if (firebase.auth.currentUser != null) AuthState.Authenticated
                    else AuthState.Unauthenticated
                onAuthStateChanged(firebase.auth) { u ->
                    _authStateFlow.value =
                        if (u == null) AuthState.Unauthenticated else AuthState.Authenticated
                }
            }
        }
    }
}
