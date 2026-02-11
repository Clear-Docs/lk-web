package com.example.testKotlin.firebase

import kotlinx.coroutines.await
import kotlin.js.Console
import kotlin.js.json

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
    fun EmailAuthProvider(): dynamic
}

@JsModule("firebaseui")
@JsNonModule
private external val firebaseUiModule: dynamic

@JsModule("firebaseui/dist/firebaseui.css")
@JsNonModule
private external val firebaseUiCss: dynamic

data class FirebaseConfig(
    val apiKey: String,
    val authDomain: String,
    val projectId: String,
    val appId: String,
    val messagingSenderId: String,
    val storageBucket: String? = null,
    val measurementId: String? = null
)

data class FirebaseContext(
    val app: dynamic,
    val auth: dynamic,
    val ui: dynamic
)

data class FirebaseProfile(
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)

fun firebaseUserProfile(user: dynamic?): FirebaseProfile? {
    if (user == null) return null
    return FirebaseProfile(
        displayName = user.displayName as? String,
        email = user.email as? String,
        photoUrl = user.photoURL as? String
    )
}

fun initializeFirebase(config: FirebaseConfig): FirebaseContext {
    // Ensure FirebaseUI styles are bundled.
    firebaseUiCss
    console.log("FirebaseUI: css module ensured")

    val options = js("{}")
    options.apiKey = config.apiKey
    options.authDomain = config.authDomain
    options.projectId = config.projectId
    options.appId = config.appId
    options.messagingSenderId = config.messagingSenderId
    config.storageBucket?.let { options.storageBucket = it }
    config.measurementId?.let { options.measurementId = it }

    val app = FirebaseApp.initializeApp(options)
    console.log("Firebase: app initialized (modular API)")
    
    val auth = FirebaseAuth.getAuth(app)
    console.log("Firebase: auth ready (modular API)")

    val authUiClass = run {
        val raw = if (jsTypeOf(firebaseUiModule) != "undefined") firebaseUiModule else js("require('firebaseui')")
        val mod = if (raw != null && jsTypeOf(raw.default) != "undefined") raw.default else raw
        console.log("FirebaseUI raw keys", if (raw != null) js("Object.keys(raw)") else "null")
        console.log("FirebaseUI mod keys", if (mod != null) js("Object.keys(mod)") else "null")
        if (jsTypeOf(mod) == "undefined") return FirebaseContext(app, auth, null)
        mod.auth?.AuthUI
            ?: mod.AuthUI
            ?: mod.default?.auth?.AuthUI
            ?: mod.default?.AuthUI
    }

    val ui = when {
        authUiClass == null -> null
        else -> {
            try {
                val existing = if (jsTypeOf(authUiClass.getInstance) != "undefined") authUiClass.getInstance() else null
                existing ?: js("new authUiClass(auth)")
            } catch (e: dynamic) {
                console.error("FirebaseUI: failed to create AuthUI instance", e)
                null
            }
        }
    }
    console.log("FirebaseUI: class resolved", authUiClass != null, "ui instance", ui != null)
    return FirebaseContext(app, auth, ui)
}

fun startFirebaseUi(ui: dynamic, containerSelector: String, providers: Array<dynamic>) {
    if (ui == null || jsTypeOf(ui.start) == "undefined") {
        console.error("FirebaseUI: ui/start is undefined, skip start")
        return
    }
    val uiConfig = js("{}")
    uiConfig.signInFlow = "popup"
    uiConfig.signInOptions = providers
    val callbacks = js("{}")
    callbacks.signInSuccessWithAuthResult = { _: dynamic, _: dynamic -> false }
    uiConfig.callbacks = callbacks
    console.log("FirebaseUI: starting widget", json("providers" to providers.map { it ?: "null" }))
    ui.start(containerSelector, uiConfig)
}

fun resetFirebaseUi(ui: dynamic) {
    if (ui != null && jsTypeOf(ui.reset) != "undefined") {
        console.log("FirebaseUI: reset")
        ui.reset()
    }
}

fun onAuthStateChanged(auth: dynamic, handler: (dynamic) -> Unit): () -> Unit {
    val unsub = FirebaseAuth.onAuthStateChanged(auth, handler)
    return {
        try {
            if (unsub != null && jsTypeOf(unsub) == "function") {
                (unsub as (() -> Unit))()
            }
        } catch (e: dynamic) {
            console.error("onAuthStateChanged: unsubscribe error", e)
        }
    }
}

suspend fun signOut(auth: dynamic) {
    FirebaseAuth.signOut(auth).await()
}

suspend fun signInWithEmailAndPassword(auth: dynamic, email: String, password: String): dynamic {
    return FirebaseAuth.signInWithEmailAndPassword(auth, email, password).await()
}

suspend fun createUserWithEmailAndPassword(auth: dynamic, email: String, password: String): dynamic {
    return FirebaseAuth.createUserWithEmailAndPassword(auth, email, password).await()
}

suspend fun signInWithGoogle(auth: dynamic): dynamic {
    val provider = FirebaseAuth.GoogleAuthProvider()
    return FirebaseAuth.signInWithPopup(auth, provider).await()
}

fun emailProvider(): dynamic = "password"

fun googleProvider(): dynamic = "google.com"
