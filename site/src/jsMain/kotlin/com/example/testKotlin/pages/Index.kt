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
    val currentPath = js("window.location.pathname") as? String
    console.log("Index: mount", "path=", currentPath, "currentUser=", firebase.auth.currentUser?.uid)

    DisposableEffect(firebase.auth) {
        console.log("Index: subscribe onAuthStateChanged", "path=", js("window.location.pathname"))
        val unsubscribe = onAuthStateChanged(firebase.auth) { firebaseUser ->
            val targetRoute = if (firebaseUser == null) "/auth" else "/profile"
            console.log(
                "Index: onAuthStateChanged",
                "uid=",
                firebaseUser?.uid,
                "targetRoute=",
                targetRoute,
                "path=",
                js("window.location.pathname")
            )
            ctx.router.tryRoutingTo(targetRoute)
        }
        onDispose {
            console.log("Index: unmount", "path=", js("window.location.pathname"))
            unsubscribe()
        }
    }
}