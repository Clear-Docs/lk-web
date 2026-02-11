package com.example.testKotlin.pages

import androidx.compose.runtime.*
import com.example.testKotlin.AuthGoogleButtonVariant
import com.example.testKotlin.AuthPrimaryButtonVariant
import com.example.testKotlin.AuthTabActiveVariant
import com.example.testKotlin.AuthTabInactiveVariant
import com.example.testKotlin.AuthToggleButtonVariant
import com.example.testKotlin.firebase.*
import com.example.testKotlin.toSitePalette
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.boxShadow
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Input

private enum class AuthMode {
    SIGN_IN,
    SIGN_UP
}

private fun authErrorToMessage(error: dynamic, isGoogleAuth: Boolean = false): String {
    val code = error?.code as? String
    return when (code) {
        "auth/invalid-email" -> "Некорректный email."
        "auth/invalid-credential" -> {
            if (isGoogleAuth) {
                "Google-вход отклонен. Проверьте настройки Google-провайдера в Firebase."
            } else {
                "Неверный email или пароль."
            }
        }
        "auth/user-not-found" -> "Пользователь не найден."
        "auth/wrong-password" -> "Неверный пароль."
        "auth/email-already-in-use" -> "Этот email уже используется."
        "auth/weak-password" -> "Слишком простой пароль."
        "auth/operation-not-allowed" -> "Google-вход не включен в Firebase (Authentication -> Sign-in method)."
        "auth/unauthorized-domain" -> "Текущий домен не добавлен в Authorized domains в Firebase."
        "auth/popup-closed-by-user" -> "Вход через Google отменен."
        "auth/popup-blocked" -> "Браузер заблокировал всплывающее окно для Google-входа."
        else -> {
            if (isGoogleAuth) {
                "Не удалось войти через Google. Код: ${code ?: "unknown"}."
            } else {
                "Не удалось выполнить авторизацию. Попробуйте еще раз."
            }
        }
    }
}

@Page("/auth")
@Composable
fun AuthPage() {
    val repository = FirebaseProvider.repository
    val authState by repository.authStateFlow.collectAsState()
    val ctx = rememberPageContext()
    val palette = ColorMode.current.toSitePalette()
    val colorPalette = ColorMode.current.toPalette()
    val inputBg = colorPalette.background.toString()
    val inputFg = colorPalette.color.toString()
    val scope = rememberCoroutineScope()
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState) {
        if (authState == AuthState.Authenticated) {
                errorMessage = null
                ctx.router.tryRoutingTo("/profile")
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
            SpanText("Вход в аккаунт", Modifier.fontSize(1.5.cssRem))

            if (authState != AuthState.Authenticated) {
                if (authState == AuthState.Loading) {
                    SpanText("Проверяем сессию...")
                    return@Column
                }
                Column(Modifier.fillMaxWidth().gap(1.cssRem)) {
                    Row(
                        Modifier.fillMaxWidth().gap(0.5.cssRem).margin(bottom = 0.25.cssRem),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                mode = AuthMode.SIGN_IN
                                errorMessage = null
                            },
                            modifier = Modifier.fillMaxWidth().flexGrow(1).padding(0.65.cssRem).borderRadius(0.65.cssRem),
                            variant = if (mode == AuthMode.SIGN_IN) AuthTabActiveVariant else AuthTabInactiveVariant,
                            enabled = !isLoading
                        ) {
                            SpanText("Вход")
                        }
                        Button(
                            onClick = {
                                mode = AuthMode.SIGN_UP
                                errorMessage = null
                            },
                            modifier = Modifier.fillMaxWidth().flexGrow(1).padding(0.65.cssRem).borderRadius(0.65.cssRem),
                            variant = if (mode == AuthMode.SIGN_UP) AuthTabActiveVariant else AuthTabInactiveVariant,
                            enabled = !isLoading
                        ) {
                            SpanText("Регистрация")
                        }
                    }

                    Input(
                        type = InputType.Email,
                        attrs = {
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
                                property("background", inputBg)
                                property("color", inputFg)
                            }
                        }
                    )

                    Row(
                        Modifier.fillMaxWidth().gap(0.5.cssRem).margin(bottom = 0.75.cssRem),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.fillMaxWidth().flexGrow(1)) {
                            Input(
                                type = if (isPasswordVisible) InputType.Text else InputType.Password,
                                attrs = {
                                    value(password)
                                    placeholder("Пароль")
                                    if (isLoading) disabled()
                                    onInput { password = it.value }
                                    style {
                                        property("width", "100%")
                                        property("padding", "0.7rem")
                                        property("border-radius", "0.6rem")
                                        property("border", "0")
                                        property("outline", "none")
                                        property("background", inputBg)
                                        property("color", inputFg)
                                    }
                                }
                            )
                        }
                        Button(
                            onClick = { isPasswordVisible = !isPasswordVisible },
                            modifier = Modifier.padding(0.7.cssRem, 0.85.cssRem).borderRadius(0.6.cssRem),
                            variant = AuthToggleButtonVariant,
                            enabled = !isLoading
                        ) {
                            SpanText(if (isPasswordVisible) "Скрыть" else "Показать")
                        }
                    }

                    if (mode == AuthMode.SIGN_UP) {
                        Row(
                            Modifier.fillMaxWidth().gap(0.5.cssRem).margin(bottom = 0.75.cssRem),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.fillMaxWidth().flexGrow(1)) {
                                Input(
                                    type = if (isConfirmPasswordVisible) InputType.Text else InputType.Password,
                                    attrs = {
                                        value(confirmPassword)
                                        placeholder("Повторите пароль")
                                        if (isLoading) disabled()
                                        onInput { confirmPassword = it.value }
                                        style {
                                            property("width", "100%")
                                            property("padding", "0.7rem")
                                            property("border-radius", "0.6rem")
                                            property("border", "0")
                                            property("outline", "none")
                                            property("background", inputBg)
                                            property("color", inputFg)
                                        }
                                    }
                                )
                            }
                            Button(
                                onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                                modifier = Modifier.padding(0.7.cssRem, 0.85.cssRem).borderRadius(0.6.cssRem),
                                variant = AuthToggleButtonVariant,
                                enabled = !isLoading
                            ) {
                                SpanText(if (isConfirmPasswordVisible) "Скрыть" else "Показать")
                            }
                        }
                    }

                    Button(
                        onClick = {
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
                                        signInWithEmailAndPassword(repository.auth, normalizedEmail, password)
                                    } else {
                                        createUserWithEmailAndPassword(repository.auth, normalizedEmail, password)
                                    }
                                } catch (e: dynamic) {
                                    errorMessage = authErrorToMessage(e, isGoogleAuth = false)
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(0.75.cssRem).borderRadius(0.7.cssRem).margin(top = 0.2.cssRem),
                        variant = AuthPrimaryButtonVariant,
                        enabled = !isLoading
                    ) {
                        SpanText(
                            if (isLoading) "Подождите..."
                            else if (mode == AuthMode.SIGN_IN) "Войти"
                            else "Создать аккаунт"
                        )
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                errorMessage = null
                                isLoading = true
                                try {
                                    signInWithGoogle(repository.auth)
                                } catch (e: dynamic) {
                                    console.error("Google sign-in failed", e?.code, e?.message, e)
                                    errorMessage = authErrorToMessage(e, isGoogleAuth = true)
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(0.75.cssRem).borderRadius(0.7.cssRem).margin(top = 0.6.cssRem),
                        variant = AuthGoogleButtonVariant,
                        enabled = !isLoading
                    ) {
                        SpanText("Войти через Google")
                    }

                    errorMessage?.let { message ->
                        SpanText(
                            message,
                            Modifier.margin(top = 0.75.cssRem).color(Colors.Red).fontSize(0.9.cssRem)
                        )
                    }
                }
            } else {
                SpanText("Перенаправляем в профиль...")
            }
        }
    }
}
