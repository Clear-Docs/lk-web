package com.cleardocs.web.firebase

import kotlin.js.Json
import kotlin.js.Promise

@JsModule("firebase/app")
@JsNonModule
external object FirebaseAppModule {
    fun initializeApp(config: FirebaseConfigData): FirebaseApp
}

@JsModule("firebase/auth")
@JsNonModule
external object FirebaseAuthModule {
    fun getAuth(app: FirebaseApp = definedExternally): Auth
    fun signInWithEmailAndPassword(auth: Auth, email: String, password: String): Promise<UserCredential>
    fun createUserWithEmailAndPassword(auth: Auth, email: String, password: String): Promise<UserCredential>
    fun signInWithPopup(auth: Auth, provider: GoogleAuthProvider): Promise<UserCredential>
    fun signOut(auth: Auth): Promise<Unit>
    fun onAuthStateChanged(auth: Auth, callback: (User?) -> Unit): () -> Unit
}

external interface FirebaseApp
external interface Auth

external interface User {
    val uid: String?
    val email: String?
    val displayName: String?
}

external interface UserCredential {
    val user: User
}

external interface FirebaseError {
    val code: String?
}

@JsModule("firebase/auth")
@JsNonModule
external class GoogleAuthProvider {
    fun setCustomParameters(parameters: Json = definedExternally): GoogleAuthProvider
}

object FirebaseClient {
    val app: FirebaseApp? = runCatching {
        require(FirebaseConfig.isConfigured()) {
            "FirebaseConfig содержит плейсхолдеры YOUR_*. Заполните реальные значения."
        }
        FirebaseAppModule.initializeApp(FirebaseConfig.config)
    }.getOrElse {
        console.error("Firebase init error", it)
        null
    }

    val auth: Auth? = app?.let { runCatching { FirebaseAuthModule.getAuth(it) }.getOrNull() }
}
