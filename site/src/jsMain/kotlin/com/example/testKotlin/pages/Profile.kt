package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.*
import ru.cleardocs.lkweb.components.sections.ProfileBlock
import ru.cleardocs.lkweb.firebase.*
import ru.cleardocs.lkweb.toSitePalette
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
    val repository = FirebaseProvider.repository
    val authState by repository.authStateFlow.collectAsState()
    val profile by repository.profileFlow.collectAsState()
    val scope = rememberCoroutineScope()
    val palette = ColorMode.current.toSitePalette()
    val ctx = rememberPageContext()

    if (authState == AuthState.Unauthenticated) {
        ctx.router.tryRoutingTo("/auth")
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

            if (authState == AuthState.Loading) {
                SpanText("Проверяем авторизацию...")
            } else if (authState == AuthState.Authenticated) {
                ProfileBlock(
                    profile = profile,
                    onSignOut = {
                        scope.launch {
                            signOut(repository.auth)
                            ctx.router.tryRoutingTo("/auth")
                        }
                    }
                )
            } else {
                SpanText("Перенаправляем на авторизацию...")
            }
        }
    }
}
