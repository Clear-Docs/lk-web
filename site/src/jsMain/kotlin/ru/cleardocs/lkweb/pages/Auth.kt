package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.*
import ru.cleardocs.lkweb.AuthGoogleButtonVariant
import ru.cleardocs.lkweb.AuthPrimaryButtonVariant
import ru.cleardocs.lkweb.AuthTabActiveVariant
import ru.cleardocs.lkweb.AuthTabInactiveVariant
import ru.cleardocs.lkweb.AuthToggleButtonVariant
import ru.cleardocs.lkweb.firebase.*
import ru.cleardocs.lkweb.toSitePalette
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import org.jetbrains.compose.web.css.LineStyle
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import ru.cleardocs.lkweb.components.layouts.PageLayout
import ru.cleardocs.lkweb.components.widgets.AuthInput
import ru.cleardocs.lkweb.components.widgets.InputLayout
import ru.cleardocs.lkweb.components.widgets.PasswordFieldWithToggle
import ru.cleardocs.lkweb.components.widgets.cardSurface
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Input
import ru.cleardocs.lkweb.toSitePalette
import ru.cleardocs.lkweb.utils.requireGuestRedirect

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
        "auth/too-many-requests" -> "Слишком много попыток. Попробуйте позже."
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
    val inputBorder = palette.cobweb.toString()
    val scope = rememberCoroutineScope()
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    if (authState == AuthState.Authenticated) {
        errorMessage = null
        requireGuestRedirect(authState, ctx.router::tryRoutingTo)
    }

    PageLayout("Вход в аккаунт") {
        Box(
            Modifier
                .fillMaxSize()
                .padding(leftRight = 1.cssRem, top = 4.cssRem),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .width(32.cssRem)
                    .gap(1.25.cssRem)
                    .cardSurface(palette),
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
                                        successMessage = null
                                    },
                                    modifier = Modifier.fillMaxWidth().flexGrow(1).padding(0.65.cssRem)
                                        .borderRadius(0.65.cssRem),
                                    variant = if (mode == AuthMode.SIGN_IN) AuthTabActiveVariant else AuthTabInactiveVariant,
                                    enabled = !isLoading
                                ) {
                                    SpanText("Вход")
                                }
                                Button(
                                    onClick = {
                                        mode = AuthMode.SIGN_UP
                                        errorMessage = null
                                        successMessage = null
                                    },
                                    modifier = Modifier.fillMaxWidth().flexGrow(1).padding(0.65.cssRem)
                                        .borderRadius(0.65.cssRem),
                                    variant = if (mode == AuthMode.SIGN_UP) AuthTabActiveVariant else AuthTabInactiveVariant,
                                    enabled = !isLoading
                                ) {
                                    SpanText("Регистрация")
                                }
                            }

                            InputLayout(label = "Email") {
                                AuthInput(
                                    type = InputType.Email,
                                    value = email,
                                    placeholder = "Email",
                                    onValueChange = { email = it },
                                    inputBg = inputBg,
                                    inputFg = inputFg,
                                    inputBorder = inputBorder,
                                    enabled = !isLoading
                                )
                            }

                            InputLayout(label = "Пароль") {
                                PasswordFieldWithToggle(
                                value = password,
                                placeholder = "Пароль",
                                onValueChange = { password = it },
                                isPasswordVisible = isPasswordVisible,
                                onToggleVisibility = { isPasswordVisible = !isPasswordVisible },
                                inputBg = inputBg,
                                inputFg = inputFg,
                                inputBorder = inputBorder,
                                enabled = !isLoading
                                )
                            }

                            if (mode == AuthMode.SIGN_UP) {
                                InputLayout(
                                    label = "Повторите пароль",
                                    error = if (password != confirmPassword && confirmPassword.isNotEmpty()) "Пароли не совпадают" else null
                                ) {
                                    PasswordFieldWithToggle(
                                    value = confirmPassword,
                                    placeholder = "Повторите пароль",
                                    onValueChange = { confirmPassword = it },
                                    isPasswordVisible = isConfirmPasswordVisible,
                                    onToggleVisibility = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                                    inputBg = inputBg,
                                    inputFg = inputFg,
                                    inputBorder = inputBorder,
                                    enabled = !isLoading
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        errorMessage = null
                                        successMessage = null
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
                                                console.log("[Auth] Вход:", normalizedEmail)
                                                signInWithEmailAndPassword(
                                                    repository.auth,
                                                    normalizedEmail,
                                                    password
                                                )
                                                console.log("[Auth] Вход успешен")
                                            } else {
                                                console.log("[Auth] Регистрация: начало, email=", normalizedEmail)
                                                val result =
                                                    createUserWithEmailAndPassword(
                                                        repository.auth,
                                                        normalizedEmail,
                                                        password
                                                    )
                                                val user = result?.user
                                                console.log(
                                                    "[Auth] Регистрация: createUser OK, user=",
                                                    user?.uid,
                                                    "emailVerified=",
                                                    user?.emailVerified
                                                )
                                                if (user != null) {
                                                    try {
                                                        console.log("[Auth] Регистрация: отправка письма верификации...")
                                                        sendEmailVerification(
                                                            user
                                                        )
                                                        console.log("[Auth] Регистрация: sendEmailVerification OK")
                                                        successMessage =
                                                            "Письмо отправлено на $normalizedEmail. Проверьте почту и папку «Спам»."
                                                    } catch (verifyErr: dynamic) {
                                                        console.error(
                                                            "[Auth] Регистрация: sendEmailVerification ошибка",
                                                            verifyErr?.code,
                                                            verifyErr?.message,
                                                            verifyErr
                                                        )
                                                        errorMessage =
                                                            "Регистрация успешна, но не удалось отправить письмо подтверждения: ${
                                                                authErrorToMessage(
                                                                    verifyErr,
                                                                    false
                                                                )
                                                            }"
                                                    }
                                                } else {
                                                    console.warn("[Auth] Регистрация: user=null")
                                                    successMessage = "Аккаунт создан. Проверьте почту."
                                                }
                                                console.log("[Auth] Регистрация: завершено")
                                            }
                                        } catch (e: dynamic) {
                                            console.error("[Auth] Ошибка:", e?.code, e?.message, e)
                                            errorMessage = authErrorToMessage(
                                                e,
                                                isGoogleAuth = false
                                            )
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(0.75.cssRem).borderRadius(0.7.cssRem)
                                    .margin(top = 0.2.cssRem),
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
                                            errorMessage = authErrorToMessage(
                                                e,
                                                isGoogleAuth = true
                                            )
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(0.75.cssRem).borderRadius(0.7.cssRem)
                                    .margin(top = 0.6.cssRem),
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
                            successMessage?.let { message ->
                                SpanText(
                                    message,
                                    Modifier.margin(top = 0.75.cssRem).color(Colors.Green).fontSize(0.9.cssRem)
                                )
                            }
                        }
                    } else {
                        successMessage?.let { message ->
                            SpanText(
                                message,
                                Modifier.margin(bottom = 0.5.cssRem).color(Colors.Green).fontSize(0.9.cssRem)
                            )
                        }
                        SpanText("Перенаправляем в профиль...")
                    }
                }
        }
    }
}
