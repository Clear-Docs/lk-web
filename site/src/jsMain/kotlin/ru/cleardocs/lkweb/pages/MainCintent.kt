package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import kotlinx.coroutines.launch
import ru.cleardocs.lkweb.components.layouts.PageLayout
import ru.cleardocs.lkweb.components.sections.ProfileMenu
import ru.cleardocs.lkweb.firebase.signOut
import ru.cleardocs.lkweb.profile.MeViewModel
import ru.cleardocs.lkweb.utils.requireProfileAuthRedirect
import ru.cleardocs.lkweb.di.kodein
import org.kodein.di.instance

@Composable
private fun MainContent(mainState: MainViewState, meViewModel: MeViewModel) {
    when (mainState) {
        MainViewState.Profile -> ProfileContent(meViewModel = meViewModel)
        MainViewState.Connectors -> ConnectorsContent()
        MainViewState.Plans -> PlansContent(meViewModel = meViewModel)
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
