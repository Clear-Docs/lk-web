package ru.cleardocs.lkweb.firebase

/**
 * Global Firebase holder used across all @Page composables.
 * The context is initialized once and reused for the app lifetime.
 */
object FirebaseProvider {
    val context: FirebaseContext by lazy(LazyThreadSafetyMode.NONE) {
        firebaseLog("FirebaseProvider", "context lazy init START")
        val ctx = initializeFirebase(defaultFirebaseConfig)
        firebaseLog("FirebaseProvider", "context init DONE")
        ctx
    }

    val repository: FirebaseRepository by lazy(LazyThreadSafetyMode.NONE) {
        firebaseLog("FirebaseProvider", "repository lazy init START")
        context
        val repo = FirebaseRepository(context)
        firebaseLog("FirebaseProvider", "repository init DONE")
        repo
    }
}
