package com.example.testKotlin.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.example.testKotlin.firebase.initializeFirebase
import com.example.testKotlin.firebase.defaultFirebaseConfig
import com.example.testKotlin.firebase.onAuthStateChanged
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext

@Page
@Composable
fun HomePage() {
    val firebase = remember {
        initializeFirebase(defaultFirebaseConfig)
    }
    val ctx = rememberPageContext()

    DisposableEffect(firebase.auth) {
        val unsubscribe = onAuthStateChanged(firebase.auth) { firebaseUser ->
            val targetRoute = if (firebaseUser == null) "/auth" else "/profile"
            ctx.router.tryRoutingTo(targetRoute)
        }
        onDispose { unsubscribe() }
    }
}