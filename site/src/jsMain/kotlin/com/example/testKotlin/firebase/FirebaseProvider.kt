package ru.cleardocs.lkweb.firebase

/**
 * Global Firebase holder used across all @Page composables.
 * The context is initialized once and reused for the app lifetime.
 */
object FirebaseProvider {
    val context: FirebaseContext by lazy(LazyThreadSafetyMode.NONE) {
        initializeFirebase(defaultFirebaseConfig)
    }

    val repository: FirebaseRepository by lazy(LazyThreadSafetyMode.NONE) {
        FirebaseRepository(context)
    }
}
