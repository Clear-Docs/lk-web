package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.*
import ru.cleardocs.lkweb.AuthGoogleButtonVariant
import ru.cleardocs.lkweb.AuthPrimaryButtonVariant
import ru.cleardocs.lkweb.AuthTabActiveVariant
import ru.cleardocs.lkweb.AuthTabInactiveVariant
import ru.cleardocs.lkweb.auth.AuthFormMode
import ru.cleardocs.lkweb.auth.AuthViewModel
import ru.cleardocs.lkweb.firebase.AuthState
import ru.cleardocs.lkweb.firebase.FirebaseProvider
import ru.cleardocs.lkweb.toSitePalette
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.width
import ru.cleardocs.lkweb.components.layouts.PageLayout
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
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
import ru.cleardocs.lkweb.rememberInputColors
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.cssRem

val AuthFormWidthStyle by ComponentStyle {
    base { Modifier.maxWidth(32.cssRem) }
    Breakpoint.MD { Modifier.width(32.cssRem) }
}

@Page("/auth")
@Composable
fun AuthPage() {
    val repository = FirebaseProvider.repository
    val authState by repository.authStateFlow.collectAsState()
    val ctx = rememberPageContext()
    val vm = remember { AuthViewModel() }
    val state by vm.state.collectAsState()
    val palette = ColorMode.current.toSitePalette()
    val inputColors = rememberInputColors()

    if (state.navigateToProfile) {
        vm.clearNavigateToProfile()
        ctx.router.tryRoutingTo("/index")
    }

    PageLayout("Вход в аккаунт") {
        Box(
            Modifier
                .fillMaxSize()
                .padding(leftRight = 0.5.cssRem, top = 4.cssRem),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .then(AuthFormWidthStyle.toModifier())
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
                                onClick = { vm.setMode(AuthFormMode.SIGN_IN) },
                                modifier = Modifier.fillMaxWidth().flexGrow(1).padding(0.65.cssRem)
                                    .borderRadius(0.65.cssRem),
                                variant = if (state.mode == AuthFormMode.SIGN_IN) AuthTabActiveVariant else AuthTabInactiveVariant,
                                enabled = !state.isLoading
                            ) {
                                SpanText("Вход")
                            }
                            Button(
                                onClick = { vm.setMode(AuthFormMode.SIGN_UP) },
                                modifier = Modifier.fillMaxWidth().flexGrow(1).padding(0.65.cssRem)
                                    .borderRadius(0.65.cssRem),
                                variant = if (state.mode == AuthFormMode.SIGN_UP) AuthTabActiveVariant else AuthTabInactiveVariant,
                                enabled = !state.isLoading
                            ) {
                                SpanText("Регистрация")
                            }
                        }

                        InputLayout(label = "Email") {
                            AuthInput(
                                type = InputType.Email,
                                value = state.email,
                                placeholder = "Email",
                                onValueChange = { vm.setEmail(it) },
                                inputBg = inputColors.background,
                                inputFg = inputColors.foreground,
                                inputBorder = inputColors.border,
                                enabled = !state.isLoading
                            )
                        }

                        InputLayout(label = "Пароль") {
                            PasswordFieldWithToggle(
                                value = state.password,
                                placeholder = "Пароль",
                                onValueChange = { vm.setPassword(it) },
                                isPasswordVisible = state.isPasswordVisible,
                                onToggleVisibility = { vm.setPasswordVisible(!state.isPasswordVisible) },
                                inputBg = inputColors.background,
                                inputFg = inputColors.foreground,
                                inputBorder = inputColors.border,
                                enabled = !state.isLoading
                            )
                        }

                        if (state.mode == AuthFormMode.SIGN_UP) {
                            InputLayout(
                                label = "Повторите пароль",
                                error = if (state.password != state.confirmPassword && state.confirmPassword.isNotEmpty()) "Пароли не совпадают" else null
                            ) {
                                PasswordFieldWithToggle(
                                    value = state.confirmPassword,
                                    placeholder = "Повторите пароль",
                                    onValueChange = { vm.setConfirmPassword(it) },
                                    isPasswordVisible = state.isConfirmPasswordVisible,
                                    onToggleVisibility = { vm.setConfirmPasswordVisible(!state.isConfirmPasswordVisible) },
                                    inputBg = inputColors.background,
                                    inputFg = inputColors.foreground,
                                    inputBorder = inputColors.border,
                                    enabled = !state.isLoading
                                )
                            }
                        }

                        Button(
                            onClick = {
                                when (state.mode) {
                                    AuthFormMode.SIGN_IN -> vm.signInWithEmail()
                                    AuthFormMode.SIGN_UP -> vm.signUpWithEmail()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(0.75.cssRem).borderRadius(0.7.cssRem)
                                .margin(top = 0.2.cssRem),
                            variant = AuthPrimaryButtonVariant,
                            enabled = !state.isLoading
                        ) {
                            SpanText(
                                if (state.isLoading) "Подождите..."
                                else if (state.mode == AuthFormMode.SIGN_IN) "Войти"
                                else "Создать аккаунт"
                            )
                        }

                        Button(
                            onClick = { vm.signInWithGoogle() },
                            modifier = Modifier.fillMaxWidth().padding(0.75.cssRem).borderRadius(0.7.cssRem)
                                .margin(top = 0.6.cssRem),
                            variant = AuthGoogleButtonVariant,
                            enabled = !state.isLoading
                        ) {
                            SpanText("Войти через Google")
                        }

                        state.errorMessage?.let { message ->
                            SpanText(
                                message,
                                Modifier.margin(top = 0.75.cssRem).color(Colors.Red).fontSize(0.9.cssRem)
                            )
                        }
                        state.successMessage?.let { message ->
                            SpanText(
                                message,
                                Modifier.margin(top = 0.75.cssRem).color(Colors.Green).fontSize(0.9.cssRem)
                            )
                        }
                    }
                } else {
                    state.successMessage?.let { message ->
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
