package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import ru.cleardocs.lkweb.components.layouts.PageLayout
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.components.sections.ProfileBlock
import ru.cleardocs.lkweb.components.sections.ProfileMenu
import ru.cleardocs.lkweb.firebase.AuthState
import ru.cleardocs.lkweb.firebase.FirebaseProvider
import ru.cleardocs.lkweb.firebase.signOut
import ru.cleardocs.lkweb.toSitePalette
import ru.cleardocs.lkweb.utils.requireAuthRedirect
import ru.cleardocs.lkweb.di.kodein
import org.kodein.di.instance

@Composable
private fun MainContent(mainState: MainViewState) {
    when (mainState) {
        MainViewState.Profile -> ProfileContent()
    }
}

@Composable
private fun ProfileContent() {
    val repository = FirebaseProvider.repository
    val authState by repository.authStateFlow.collectAsState()
    val profile by repository.profileFlow.collectAsState()
    val palette = ColorMode.current.toSitePalette()

    Column(
        Modifier
            .flexGrow(1)
            .fillMaxSize()
            .gap(1.25.cssRem)
            .cardSurface(palette),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpanText("Профиль", Modifier.fontSize(1.5.cssRem))

        if (authState == AuthState.Loading) {
            SpanText("Проверяем авторизацию...")
        } else if (authState == AuthState.Authenticated) {
            ProfileBlock(profile = profile)
        } else {
            SpanText("Перенаправляем на авторизацию...")
        }
    }
}

@Page("/profile")
@Composable
fun ProfilePage() {
    val repository = FirebaseProvider.repository
    val authState by repository.authStateFlow.collectAsState()
    val scope = rememberCoroutineScope()
    val ctx = rememberPageContext()

    requireAuthRedirect(authState, ctx.router::tryRoutingTo)

    val onSignOut: () -> Unit = {
        scope.launch {
            signOut(repository.auth)
            ctx.router.tryRoutingTo("/auth")
        }
        Unit
    }

    val menuViewModel by kodein.instance<MenuViewModel>()
    val mainState by menuViewModel.stateFlow.collectAsState()

    PageLayout("Профиль") {
        Box(
            Modifier
                .flexGrow(1)
                .fillMaxWidth()
                .padding(2.cssRem),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .flexGrow(1)
                    .fillMaxSize()
                    .gap(1.cssRem)
            ) {
                Row(
                    Modifier
                        .flexGrow(1)
                        .fillMaxSize()
                        .gap(1.25.cssRem),
                    verticalAlignment = Alignment.Top
                ) {
                    ProfileMenu(
                        modifier = Modifier
                            .flexGrow(1)
                            .fillMaxHeight()
                            .width(25.percent)
                            .flexShrink(0)
                            .displayIfAtLeast(Breakpoint.MD),
                        onSignOut = onSignOut
                    )

                    MainContent(mainState = mainState)
                }
            }
        }
    }
}
