package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import ru.cleardocs.lkweb.components.layouts.PageLayout
import ru.cleardocs.lkweb.components.sections.ProfileMenu
import ru.cleardocs.lkweb.di.kodein
import ru.cleardocs.lkweb.firebase.AuthState
import ru.cleardocs.lkweb.firebase.FirebaseProvider
import ru.cleardocs.lkweb.firebase.signOut
import ru.cleardocs.lkweb.profile.MeViewModel
import ru.cleardocs.lkweb.utils.requireAuthRedirect
import ru.cleardocs.lkweb.utils.requireProfileAuthRedirect
import org.kodein.di.instance

@Page(routeOverride = "/index")
@Composable
fun HomePage() {
    val repository = FirebaseProvider.repository
    val authState by repository.authStateFlow.collectAsState()
    val ctx = rememberPageContext()

    requireAuthRedirect(authState, ctx.router::tryRoutingTo)

    if (authState == AuthState.Loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SpanText("Загрузка...", Modifier.fontSize(1.25.cssRem))
            }
        }
        return
    }

    if (authState == AuthState.Authenticated) {
        val meViewModel = remember { MeViewModel() }
        val profileAuthState by meViewModel.authState.collectAsState()
        val scope = rememberCoroutineScope()

        requireProfileAuthRedirect(profileAuthState, ctx.router::tryRoutingTo)

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
}
