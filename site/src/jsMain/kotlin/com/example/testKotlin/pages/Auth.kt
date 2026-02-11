package com.example.testKotlin.pages

import androidx.compose.runtime.*
import com.example.testKotlin.components.sections.AuthUiContainer
import com.example.testKotlin.firebase.*
import com.example.testKotlin.toSitePalette
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem

@Page("/auth")
@Composable
fun AuthPage() {
    val firebase = remember { initializeFirebase(defaultFirebaseConfig) }
    var user by remember { mutableStateOf<dynamic>(firebase.auth.currentUser) }
    val palette = ColorMode.current.toSitePalette()

    DisposableEffect(firebase.auth) {
        val unsubscribe = onAuthStateChanged(firebase.auth) { firebaseUser ->
            user = firebaseUser
        }
        onDispose { unsubscribe() }
    }

        if (user == null) {
            val providers = listOfNotNull(emailProvider(), googleProvider()).toTypedArray()
            startFirebaseUi(firebase.ui, "#firebaseui-auth-container", providers)
        } else {
            resetFirebaseUi(firebase.ui)
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
            SpanText("Firebase Auth", Modifier.fontSize(1.5.cssRem))
            AuthUiContainer()
        }
    }
}
