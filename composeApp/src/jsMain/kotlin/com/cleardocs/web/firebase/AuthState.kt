package com.cleardocs.web.firebase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.await

// Firebase Modular SDK v11.x
@JsModule("firebase/app")
@JsNonModule
external object FirebaseApp {
    fun initializeApp(options: dynamic): dynamic
}

@JsModule("firebase/auth")
@JsNonModule
external object FirebaseAuth {
    fun getAuth(app: dynamic = definedExternally): dynamic
    fun onAuthStateChanged(auth: dynamic, callback: (dynamic) -> Unit): dynamic
    fun signOut(auth: dynamic): dynamic
    fun signInWithPopup(auth: dynamic, provider: dynamic): dynamic
    fun signInWithEmailAndPassword(auth: dynamic, email: String, password: String): dynamic
    fun createUserWithEmailAndPassword(auth: dynamic, email: String, password: String): dynamic
    fun GoogleAuthProvider(): dynamic
}

data class FirebaseConfig(
    val apiKey: String,
    val authDomain: String,
    val projectId: String,
    val appId: String,
    val messagingSenderId: String,
    val storageBucket: String? = null,
    val measurementId: String? = null
)

object AuthState {
    // Firebase configuration
    private val firebaseConfig = defaultFirebaseConfig

    private var app: dynamic = null
    private var auth: dynamic = null
    private var unsubscribe: (() -> Unit)? = null

    // Reactive state
    var isAuthenticated by mutableStateOf(false)
        private set
    
    var userDisplayName by mutableStateOf<String?>(null)
        private set
    
    var userEmail by mutableStateOf<String?>(null)
        private set

    var userPhotoUrl by mutableStateOf<String?>(null)
        private set

    /**
     * Initialize Firebase and set up auth state listener
     */
    fun init() {
        if (app != null) {
            console.log("Firebase already initialized")
            return
        }

        try {
            val options = js("{}")
            options.apiKey = firebaseConfig.apiKey
            options.authDomain = firebaseConfig.authDomain
            options.projectId = firebaseConfig.projectId
            options.appId = firebaseConfig.appId
            options.messagingSenderId = firebaseConfig.messagingSenderId
            firebaseConfig.storageBucket?.let { options.storageBucket = it }
            firebaseConfig.measurementId?.let { options.measurementId = it }

            app = FirebaseApp.initializeApp(options)
            auth = FirebaseAuth.getAuth(app)
            
            console.log("Firebase initialized (modular API)")

            // Set up auth state listener
            val unsubscribeFn = FirebaseAuth.onAuthStateChanged(auth) { user ->
                if (user != null) {
                    isAuthenticated = true
                    userDisplayName = user.displayName as? String
                    userEmail = user.email as? String
                    userPhotoUrl = user.photoURL as? String
                    console.log("User signed in:", userEmail ?: "unknown")
                } else {
                    isAuthenticated = false
                    userDisplayName = null
                    userEmail = null
                    userPhotoUrl = null
                    console.log("User signed out")
                }
            }
            
            unsubscribe = { unsubscribeFn?.invoke() }
            console.log("Auth state listener set up")
            
        } catch (e: Throwable) {
            console.error("Failed to initialize Firebase:", e)
            throw e
        }
    }

    /**
     * Sign in with email and password
     */
    suspend fun signIn(email: String, password: String) {
        if (auth == null) init()
        try {
            val result = FirebaseAuth.signInWithEmailAndPassword(auth, email, password).await()
            console.log("Sign in successful:", email)
        } catch (e: dynamic) {
            console.error("Sign in failed:", e)
            throw Exception(translateError(e))
        }
    }

    /**
     * Register with email and password
     */
    suspend fun register(email: String, password: String) {
        if (auth == null) init()
        try {
            val result = FirebaseAuth.createUserWithEmailAndPassword(auth, email, password).await()
            console.log("Registration successful:", email)
        } catch (e: dynamic) {
            console.error("Registration failed:", e)
            throw Exception(translateError(e))
        }
    }

    /**
     * Sign in with Google popup
     */
    suspend fun signInWithGoogle() {
        if (auth == null) init()
        try {
            val provider = FirebaseAuth.GoogleAuthProvider()
            val result = FirebaseAuth.signInWithPopup(auth, provider).await()
            console.log("Google sign in successful")
        } catch (e: dynamic) {
            console.error("Google sign in failed:", e)
            throw Exception(translateError(e))
        }
    }

    /**
     * Sign out current user
     */
    suspend fun signOut() {
        if (auth == null) return
        try {
            FirebaseAuth.signOut(auth).await()
            console.log("Sign out successful")
        } catch (e: dynamic) {
            console.error("Sign out failed:", e)
            throw Exception(translateError(e))
        }
    }

    /**
     * Translate Firebase error codes to Russian messages
     */
    fun translateError(error: dynamic): String {
        val code = error?.code as? String ?: return "Неизвестная ошибка"
        
        return when (code) {
            "auth/invalid-email" -> "Неверный формат email"
            "auth/user-disabled" -> "Этот аккаунт отключен"
            "auth/user-not-found" -> "Пользователь не найден"
            "auth/wrong-password" -> "Неверный пароль"
            "auth/email-already-in-use" -> "Email уже используется"
            "auth/operation-not-allowed" -> "Операция не разрешена"
            "auth/weak-password" -> "Слишком простой пароль (минимум 6 символов)"
            "auth/too-many-requests" -> "Слишком много попыток, попробуйте позже"
            "auth/network-request-failed" -> "Ошибка сети, проверьте подключение"
            "auth/popup-closed-by-user" -> "Окно авторизации закрыто"
            "auth/cancelled-popup-request" -> "Запрос отменен"
            "auth/popup-blocked" -> "Всплывающее окно заблокировано браузером"
            else -> "Ошибка: ${error?.message ?: code}"
        }
    }

    /**
     * Clean up resources
     */
    fun dispose() {
        unsubscribe?.invoke()
        unsubscribe = null
    }
}
