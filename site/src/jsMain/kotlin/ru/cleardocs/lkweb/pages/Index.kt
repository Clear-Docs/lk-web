package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.firebase.AuthState
import ru.cleardocs.lkweb.firebase.FirebaseProvider
import ru.cleardocs.lkweb.utils.requireAuthRedirect
import ru.cleardocs.lkweb.utils.requireGuestRedirect

@Page(routeOverride = "/index")
@Composable
fun HomePage() {
    val repository = FirebaseProvider.repository
    val authState by repository.authStateFlow.collectAsState()
    val ctx = rememberPageContext()

    requireAuthRedirect(authState, ctx.router::tryRoutingTo)
    requireGuestRedirect(authState, ctx.router::tryRoutingTo)

    if (authState == AuthState.Loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SpanText("Загрузка...", Modifier.fontSize(1.25.cssRem))
            }
        }
    }
}