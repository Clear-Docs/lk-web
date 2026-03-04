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
        firebaseLog("FirebaseRepository", "init START")
        CoroutineScope(Dispatchers.Default).launch {
            try {
                firebaseLog("FirebaseRepository", "calling authStateReady...")
                authStateReady(firebase.auth)
                val user = firebase.auth.currentUser
                firebaseLog("FirebaseRepository", "authStateReady DONE", "currentUser.uid=", user?.uid, "currentUser.email=", user?.email)
                val initialState = if (user != null) AuthState.Authenticated else AuthState.Unauthenticated
                firebaseLog("FirebaseRepository", "setting initial authState=", initialState, "(user != null:", user != null, ")")
                _authStateFlow.value = initialState
                firebaseLog("FirebaseRepository", "registering onAuthStateChanged")
                onAuthStateChanged(firebase.auth) { u ->
                    val prevState = _authStateFlow.value
                    val newState = if (u == null) AuthState.Unauthenticated else AuthState.Authenticated
                    firebaseLog("FirebaseRepository", "onAuthStateChanged callback", "user.uid=", u?.uid, "prevState=", prevState, "newState=", newState)
                    _authStateFlow.value = newState
                }
            } catch (e: Throwable) {
                firebaseLog("FirebaseRepository", "init ERROR", e)
                console.error("[FirebaseRepository.init] authStateReady failed", e)
                val user = firebase.auth.currentUser
                firebaseLog("FirebaseRepository", "fallback: auth.currentUser=", user?.uid)
                _authStateFlow.value =
                    if (user != null) AuthState.Authenticated
                    else AuthState.Unauthenticated
                onAuthStateChanged(firebase.auth) { u ->
                    val prevState = _authStateFlow.value
                    val newState = if (u == null) AuthState.Unauthenticated else AuthState.Authenticated
                    firebaseLog("FirebaseRepository", "onAuthStateChanged callback (fallback)", "user.uid=", u?.uid, "prevState=", prevState, "newState=", newState)
                    _authStateFlow.value = newState
                }
            }
        }
    }
}
