package com.example.testKotlin.pages

import androidx.compose.runtime.*
import com.example.testKotlin.firebase.initializeFirebase
import com.example.testKotlin.firebase.defaultFirebaseConfig
import com.example.testKotlin.firebase.onAuthStateChanged
import com.example.testKotlin.toSitePalette
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem

@Page
@Composable
fun HomePage() {
    val firebase = remember {
        initializeFirebase(defaultFirebaseConfig)
    }
    var user by remember {
        val currentUser = firebase.auth.currentUser
        mutableStateOf<dynamic>(currentUser)
    }
    val palette = ColorMode.current.toSitePalette()
    val ctx = rememberPageContext()

    DisposableEffect(firebase.auth) {
        val unsubscribe = onAuthStateChanged(firebase.auth) { firebaseUser ->
            // Если пользователь авторизовался, перенаправляем на страницу профиля
            if (user == null) {
                ctx.router.tryRoutingTo("/auth")
            } else {
                ctx.router.tryRoutingTo("/profile")
            }
        }
        onDispose {
            unsubscribe()
        }
    }
}