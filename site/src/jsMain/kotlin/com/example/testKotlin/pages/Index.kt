package com.example.testKotlin.pages

import androidx.compose.runtime.*
import com.example.testKotlin.firebase.*
import com.example.testKotlin.toSitePalette
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun HomePage() {
    val firebase = remember { initializeFirebase(defaultFirebaseConfig) }
    var user by remember { mutableStateOf<dynamic>(firebase.auth.currentUser) }
    val scope = rememberCoroutineScope()
    val palette = ColorMode.current.toSitePalette()

    DisposableEffect(firebase.auth) {
        val unsubscribe = onAuthStateChanged(firebase.auth) { firebaseUser ->
            user = firebaseUser
        }
        onDispose { unsubscribe() }
    }

    LaunchedEffect(user) {
        if (user == null) {
            val providers = listOfNotNull(emailProvider(), googleProvider()).toTypedArray()
            startFirebaseUi(firebase.ui, "#firebaseui-auth-container", providers)
        } else {
            resetFirebaseUi(firebase.ui)
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
            SpanText("Firebase Auth", Modifier.fontSize(1.5.cssRem))
            SpanText("Замените конфиг своими ключами Firebase.", Modifier.color(Colors.Gray))

            if (user == null) {
                AuthUiContainer()
            } else {
                ProfileBlock(
                    profile = firebaseUserProfile(user),
                    onSignOut = {
                        scope.launch { signOut(firebase.auth) }
                    }
                )
            }
        }
    }
}

@Composable
private fun AuthUiContainer() {
    val palette = ColorMode.current.toSitePalette()
    Column(Modifier.fillMaxWidth().gap(1.cssRem)) {
        SpanText("Вход или регистрация через FirebaseUI", Modifier.fontSize(1.1.cssRem))
        Div(
            Modifier
                .fillMaxWidth()
                .minHeight(18.cssRem)
                .padding(0.5.cssRem)
                .borderRadius(0.75.cssRem)
                .backgroundColor(palette.nearBackground)
                .toAttrs { id("firebaseui-auth-container") }
        )
    }
}

@Composable
private fun ProfileBlock(profile: FirebaseProfile?, onSignOut: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().gap(1.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpanText("Вы вошли", Modifier.fontSize(1.1.cssRem))
        profile?.photoUrl?.let {
            Img(src = it, alt = "avatar", attrs = {
                style {
                    width(4.cssRem)
                    height(4.cssRem)
                    borderRadius(50.percent)
                    property("object-fit", "cover")
                }
            })
        }
        SpanText(profile?.displayName ?: "Без имени")
        profile?.email?.let { SpanText(it, Modifier.color(Colors.Gray)) }
        Spacer()
        Button(onClick = { _ -> onSignOut() }) { Text("Выйти") }
    }
}