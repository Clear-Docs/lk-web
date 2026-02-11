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
import kotlin.js.console

@Page
@Composable
fun HomePage() {
    console.log("[HomePage] Композиция начата")

    val firebase = remember {
        console.log("[HomePage] Инициализация Firebase")
        initializeFirebase(defaultFirebaseConfig)
    }
    var user by remember {
        val currentUser = firebase.auth.currentUser
        console.log("[HomePage] Начальное состояние user:", currentUser, "email:", currentUser?.email ?: "null")
        mutableStateOf<dynamic>(currentUser)
    }
    val palette = ColorMode.current.toSitePalette()
    val ctx = rememberPageContext()
    console.log("[HomePage] ColorMode:", ColorMode.current.name, "path:", ctx.route.path)

    DisposableEffect(firebase.auth) {
        console.log("[HomePage] DisposableEffect: подписка на onAuthStateChanged")
        val unsubscribe = onAuthStateChanged(firebase.auth) { firebaseUser ->
            console.log("[HomePage] onAuthStateChanged: firebaseUser=", firebaseUser, "email=", firebaseUser?.email ?: "null", "user(state)=", user)
            // Если пользователь авторизовался, перенаправляем на страницу профиля
            if (user == null) {
                console.log("[HomePage] user==null -> редирект на /auth")
                ctx.router.tryRoutingTo("/auth")
            } else {
                console.log("[HomePage] user!=null -> редирект на /profile")
                ctx.router.tryRoutingTo("/profile")
            }
        }
        onDispose {
            console.log("[HomePage] DisposableEffect: отписка от onAuthStateChanged")
            unsubscribe()
        }
    }

    console.log("[HomePage] Рендер UI: Box + Column")
    Box(
        Modifier
            .fillMaxSize()
            .padding(3.cssRem),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .maxWidth(32.cssRem)
                .gap(1.25.cssRem)
                .padding(2.cssRem)
                .borderRadius(1.25.cssRem)
                .backgroundColor(palette.nearBackground)
                .boxShadow(
                    blurRadius = 1.2.cssRem,
                    color = palette.cobweb
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText("Главная страница", Modifier.fontSize(1.5.cssRem))
            SpanText("Добро пожаловать!")
        }
    }
}