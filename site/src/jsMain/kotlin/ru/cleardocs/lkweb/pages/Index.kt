package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Div
import ru.cleardocs.lkweb.toSitePalette
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import ru.cleardocs.lkweb.components.layouts.PageLayout
import ru.cleardocs.lkweb.components.sections.ProfileMenu
import ru.cleardocs.lkweb.di.kodein
import ru.cleardocs.lkweb.firebase.AuthState
import ru.cleardocs.lkweb.firebase.FirebaseProvider
import ru.cleardocs.lkweb.firebase.signOut
import ru.cleardocs.lkweb.profile.MeViewModel
import ru.cleardocs.lkweb.profile.ProfileAuthState
import org.kodein.di.instance


@Page("/")
@Composable
fun HomePage() {
    val repository = FirebaseProvider.repository
    val authState by repository.authStateFlow.collectAsState()
    val ctx = rememberPageContext()
    val navigateToAuth: () -> Unit = { ctx.router.tryRoutingTo("/auth") }

    when (authState) {
        AuthState.Loading -> HomeLoadingContent()
        AuthState.Unauthenticated -> HomeRedirectingToAuthContent(navigateToAuth)
        AuthState.Authenticated -> {
            val meViewModel = remember { MeViewModel() }
            val profileAuthState by meViewModel.authState.collectAsState()

            when (profileAuthState) {
                ProfileAuthState.Loading -> { }
                ProfileAuthState.Unauthenticated -> HomeRedirectingToAuthContent(navigateToAuth)
                ProfileAuthState.Authenticated -> HomeProfileMainContent(meViewModel, navigateToAuth)
            }
        }
    }
}

@Composable
private fun HomeLoadingContent() {
    val palette = ColorMode.current.toSitePalette()
    val brandColor = palette.brand.primary.toString()

    DisposableEffect(Unit) {
        if (document.getElementById("home-loading-spinner-keyframes") == null) {
            val style = document.createElement("style").unsafeCast<org.w3c.dom.HTMLStyleElement>()
            style.id = "home-loading-spinner-keyframes"
            style.appendChild(
                document.createTextNode(
                    """
                    @keyframes home-loading-spin {
                        to { transform: rotate(360deg); }
                    }
                """.trimIndent()
                )
            )
            document.head?.appendChild(style)
        }
        onDispose { }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Div(
            attrs = {
                style {
                    property("width", "48px")
                    property("height", "48px")
                    property("border", "4px solid ${palette.cobweb}")
                    property("border-top-color", brandColor)
                    property("border-radius", "50%")
                    property("animation", "home-loading-spin 0.8s linear infinite")
                }
            }
        )
    }
}

@Composable
private fun HomeRedirectingToAuthContent(navigateToAuth: () -> Unit) {
    navigateToAuth()
}

@Composable
private fun HomeProfileMainContent(meViewModel: MeViewModel, navigateToAuth: () -> Unit) {
    val scope = rememberCoroutineScope()
    val onSignOut: () -> Unit = {
        scope.launch {
            signOut(meViewModel.repository.auth)
            navigateToAuth()
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
                .padding(1.cssRem),
            contentAlignment = Alignment.Center
        ) {
            Row(
                Modifier
                    .flexGrow(1)
                    .fillMaxSize()
                    .gap(1.cssRem),
                verticalAlignment = Alignment.Top
            ) {
                ProfileMenu(
                    modifier = Modifier
                        .flexGrow(1)
                        .fillMaxHeight()
                        .width(20.percent)
                        .flexShrink(0)
                        .displayIfAtLeast(Breakpoint.MD),
                    onSignOut = onSignOut
                )

                MainContent(mainState = mainState, meViewModel = meViewModel)
            }
        }
    }
}