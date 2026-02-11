package com.example.testKotlin.pages

import androidx.compose.runtime.*
import com.example.testKotlin.components.sections.ProfileBlock
import com.example.testKotlin.firebase.*
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.cssRem

@Page("/profile")
@Composable
fun ProfilePage() {
    val firebase = remember { initializeFirebase(defaultFirebaseConfig) }
    var user by remember { mutableStateOf<dynamic>(firebase.auth.currentUser) }
    val currentPath = js("window.location.pathname") as? String
    console.log("Profile: mount", "path=", currentPath, "currentUser=", firebase.auth.currentUser?.uid)
    val scope = rememberCoroutineScope()
    val palette = ColorMode.current.toSitePalette()
    val ctx = rememberPageContext()

    DisposableEffect(firebase.auth) {
        console.log("Profile: subscribe onAuthStateChanged", "path=", js("window.location.pathname"))
        val unsubscribe = onAuthStateChanged(firebase.auth) { firebaseUser ->
            console.log(
                "Profile: onAuthStateChanged",
                "uid=",
                firebaseUser?.uid,
                "path=",
                js("window.location.pathname")
            )
            user = firebaseUser
            // Если пользователь не авторизован, перенаправляем на страницу авторизации
            if (firebaseUser == null) {
                console.log("Profile: redirect to /auth from callback", "path=", js("window.location.pathname"))
                ctx.router.tryRoutingTo("/auth")
            }
        }
        onDispose {
            console.log("Profile: unmount", "path=", js("window.location.pathname"))
            unsubscribe()
        }
    }

    // Проверяем при загрузке страницы
    LaunchedEffect(Unit) {
        console.log("Profile: LaunchedEffect check", "user=", user?.uid, "path=", js("window.location.pathname"))
        if (user == null) {
            console.log("Profile: redirect to /auth from LaunchedEffect", "path=", js("window.location.pathname"))
            ctx.router.tryRoutingTo("/auth")
        }
    }

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
            SpanText("Профиль", Modifier.fontSize(1.5.cssRem))

            if (user != null) {
                ProfileBlock(
                    profile = firebaseUserProfile(user),
                    onSignOut = {
                        scope.launch {
                            signOut(firebase.auth)
                            ctx.router.tryRoutingTo("/auth")
                        }
                    }
                )
            }
        }
    }
}
