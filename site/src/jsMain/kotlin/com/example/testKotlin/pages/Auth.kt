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
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text

private enum class AuthMode {
    SIGN_IN,
    SIGN_UP
}

private fun authErrorToMessage(error: dynamic): String {
    return when (error?.code as? String) {
        "auth/invalid-email" -> "Некорректный email."
        "auth/invalid-credential" -> "Неверный email или пароль."
        "auth/user-not-found" -> "Пользователь не найден."
        "auth/wrong-password" -> "Неверный пароль."
        "auth/email-already-in-use" -> "Этот email уже используется."
        "auth/weak-password" -> "Слишком простой пароль."
        "auth/popup-closed-by-user" -> "Вход через Google отменен."
        "auth/popup-blocked" -> "Браузер заблокировал всплывающее окно для Google-входа."
        else -> "Не удалось выполнить авторизацию. Попробуйте еще раз."
    }
}

@Page("/auth")
@Composable
fun AuthPage() {
    val firebase = remember { initializeFirebase(defaultFirebaseConfig) }
    var user by remember { mutableStateOf<dynamic>(firebase.auth.currentUser) }
    val palette = ColorMode.current.toSitePalette()
    val colorPalette = ColorMode.current.toPalette()
    val scope = rememberCoroutineScope()
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(firebase.auth) {
        val unsubscribe = onAuthStateChanged(firebase.auth) { firebaseUser ->
            user = firebaseUser
            if (firebaseUser != null) {
                errorMessage = null
            }
        }
        onDispose { unsubscribe() }
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
            SpanText("Вход в аккаунт", Modifier.fontSize(1.5.cssRem))

            if (user == null) {
                Div(
                    Modifier
                        .fillMaxWidth()
                        .toAttrs()
                ) {
                    Div({
                        style {
                            property("display", "flex")
                            property("gap", "0.5rem")
                            property("margin-bottom", "1rem")
                        }
                    }) {
                        Button(attrs = {
                            if (isLoading) disabled()
                            onClick {
                                mode = AuthMode.SIGN_IN
                                errorMessage = null
                            }
                            style {
                                property("width", "50%")
                                property("padding", "0.65rem")
                                property("border-radius", "0.65rem")
                                property("border", "0")
                                property("cursor", "pointer")
                                property(
                                    "background",
                                    if (mode == AuthMode.SIGN_IN) palette.brand.primary.toString() else palette.nearBackground.toString()
                                )
                                property("color", if (mode == AuthMode.SIGN_IN) colorPalette.background.toString() else colorPalette.color.toString())
                            }
                        }) {
                            Text("Вход")
                        }
                        Button(attrs = {
                            if (isLoading) disabled()
                            onClick {
                                mode = AuthMode.SIGN_UP
                                errorMessage = null
                            }
                            style {
                                property("width", "50%")
                                property("padding", "0.65rem")
                                property("border-radius", "0.65rem")
                                property("border", "0")
                                property("cursor", "pointer")
                                property(
                                    "background",
                                    if (mode == AuthMode.SIGN_UP) palette.brand.primary.toString() else palette.nearBackground.toString()
                                )
                                property("color", if (mode == AuthMode.SIGN_UP) colorPalette.background.toString() else colorPalette.color.toString())
                            }
                        }) {
                            Text("Регистрация")
                        }
                    }

                    Input(type = InputType.Email, attrs = {
                        value(email)
                        placeholder("Email")
                        if (isLoading) disabled()
                        onInput { email = it.value }
                        style {
                            property("width", "100%")
                            property("padding", "0.7rem")
                            property("margin-bottom", "0.75rem")
                            property("border-radius", "0.6rem")
                            property("border", "0")
                            property("outline", "none")
                            property("background", colorPalette.background.toString())
                            property("color", colorPalette.color.toString())
                        }
                    })

                    Input(type = InputType.Password, attrs = {
                        value(password)
                        placeholder("Пароль")
                        if (isLoading) disabled()
                        onInput { password = it.value }
                        style {
                            property("width", "100%")
                            property("padding", "0.7rem")
                            property("margin-bottom", "0.75rem")
                            property("border-radius", "0.6rem")
                            property("border", "0")
                            property("outline", "none")
                            property("background", colorPalette.background.toString())
                            property("color", colorPalette.color.toString())
                        }
                    })

                    if (mode == AuthMode.SIGN_UP) {
                        Input(type = InputType.Password, attrs = {
                            value(confirmPassword)
                            placeholder("Повторите пароль")
                            if (isLoading) disabled()
                            onInput { confirmPassword = it.value }
                            style {
                                property("width", "100%")
                                property("padding", "0.7rem")
                                property("margin-bottom", "0.75rem")
                                property("border-radius", "0.6rem")
                                property("border", "0")
                                property("outline", "none")
                                property("background", colorPalette.background.toString())
                                property("color", colorPalette.color.toString())
                            }
                        })
                    }

                    Button(attrs = {
                        if (isLoading) disabled()
                        onClick {
                            scope.launch {
                                errorMessage = null
                                val normalizedEmail = email.trim()
                                if (normalizedEmail.isEmpty() || password.isEmpty()) {
                                    errorMessage = "Заполните email и пароль."
                                    return@launch
                                }
                                if (mode == AuthMode.SIGN_UP) {
                                    if (password.length < 6) {
                                        errorMessage = "Пароль должен содержать минимум 6 символов."
                                        return@launch
                                    }
                                    if (password != confirmPassword) {
                                        errorMessage = "Пароли не совпадают."
                                        return@launch
                                    }
                                }

                                isLoading = true
                                try {
                                    if (mode == AuthMode.SIGN_IN) {
                                        signInWithEmailAndPassword(firebase.auth, normalizedEmail, password)
                                    } else {
                                        createUserWithEmailAndPassword(firebase.auth, normalizedEmail, password)
                                    }
                                } catch (e: dynamic) {
                                    errorMessage = authErrorToMessage(e)
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                        style {
                            property("width", "100%")
                            property("padding", "0.75rem")
                            property("border-radius", "0.7rem")
                            property("border", "0")
                            property("margin-top", "0.2rem")
                            property("cursor", "pointer")
                            property("background", palette.brand.primary.toString())
                            property("color", colorPalette.background.toString())
                            property("font-weight", "600")
                        }
                    }) {
                        Text(if (isLoading) "Подождите..." else if (mode == AuthMode.SIGN_IN) "Войти" else "Создать аккаунт")
                    }

                    Button(attrs = {
                        if (isLoading) disabled()
                        onClick {
                            scope.launch {
                                errorMessage = null
                                isLoading = true
                                try {
                                    signInWithGoogle(firebase.auth)
                                } catch (e: dynamic) {
                                    errorMessage = authErrorToMessage(e)
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                        style {
                            property("width", "100%")
                            property("padding", "0.75rem")
                            property("border-radius", "0.7rem")
                            property("border", "0")
                            property("margin-top", "0.6rem")
                            property("cursor", "pointer")
                            property("background", colorPalette.background.toString())
                            property("color", colorPalette.color.toString())
                            property("font-weight", "600")
                        }
                    }) {
                        Text("Войти через Google")
                    }

                    errorMessage?.let { message ->
                        Div({
                            style {
                                property("margin-top", "0.75rem")
                                property("color", "#d93025")
                                property("font-size", "0.9rem")
                            }
                        }) {
                            Text(message)
                        }
                    }
                }
            } else {
                ProfileBlock(
                    profile = firebaseUserProfile(user),
                    onSignOut = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                signOut(firebase.auth)
                            } catch (e: dynamic) {
                                errorMessage = "Не удалось выйти из аккаунта."
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                )
            }
        }
    }
}
