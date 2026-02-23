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
import ru.cleardocs.lkweb.firebase.signOut
import ru.cleardocs.lkweb.connectors.ConnectorsViewState
import ru.cleardocs.lkweb.connectors.ConnectorsViewModel
import ru.cleardocs.lkweb.profile.MeViewModel
import ru.cleardocs.lkweb.profile.ProfileAuthState
import ru.cleardocs.lkweb.toSitePalette
import ru.cleardocs.lkweb.utils.requireProfileAuthRedirect
import ru.cleardocs.lkweb.di.kodein
import org.kodein.di.instance

@Composable
private fun MainContent(mainState: MainViewState, meViewModel: MeViewModel) {
    when (mainState) {
        MainViewState.Profile -> ProfileContent(meViewModel = meViewModel)
        MainViewState.Connectors -> ConnectorsContent()
    }
}

@Composable
private fun ProfileContent(meViewModel: MeViewModel) {
    val authState by meViewModel.authState.collectAsState()
    val me by meViewModel.me.collectAsState()
    val meLoading by meViewModel.loading.collectAsState()
    val meError by meViewModel.error.collectAsState()
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

        when (authState) {
            ProfileAuthState.Loading -> SpanText("Проверяем авторизацию...")
            ProfileAuthState.Unauthenticated -> SpanText("Перенаправляем на авторизацию...")
            ProfileAuthState.Authenticated -> when {
                meLoading -> SpanText("Загрузка профиля...")
                meError != null -> SpanText("Ошибка: $meError")
                else -> ProfileBlock(meDto = me)
            }
        }
    }
}

@Composable
private fun ConnectorsContent() {
    val connectorsViewModel = remember { ConnectorsViewModel() }
    val state by connectorsViewModel.state.collectAsState()
    val palette = ColorMode.current.toSitePalette()

    Column(
        Modifier
            .flexGrow(1)
            .fillMaxSize()
            .gap(1.25.cssRem)
            .cardSurface(palette),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpanText("Коннекторы", Modifier.fontSize(1.5.cssRem))

        when (val s = state) {
            is ConnectorsViewState.Loading -> {}
            is ConnectorsViewState.GotoAuth ->
                SpanText("Перенаправляем на авторизацию...")
            is ConnectorsViewState.Error ->
                SpanText("Ошибка: ${s.message}")
            is ConnectorsViewState.ConnectorsData -> {
                val connectors = s.connectors
                if (connectors.isEmpty()) {
                    SpanText("Нет коннекторов.")
                } else {
                    Column(Modifier.fillMaxWidth().gap(0.5.cssRem)) {
                        connectors.forEach { c -> SpanText("${c.name} (${c.type})") }
                    }
                }
            }
        }
    }
}

@Page("/profile")
@Composable
fun ProfilePage() {
    val meViewModel = remember { MeViewModel() }
    val authState by meViewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()
    val ctx = rememberPageContext()

    requireProfileAuthRedirect(authState, ctx.router::tryRoutingTo)

    val onSignOut: () -> Unit = {
        scope.launch {
            signOut(meViewModel.repository.auth)
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

                    MainContent(mainState = mainState, meViewModel = meViewModel)
                }
            }
        }
    }
}
