package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw
import ru.cleardocs.lkweb.chat.ChatCredentialsViewModel
import ru.cleardocs.lkweb.components.sections.ChatBlock
import ru.cleardocs.lkweb.toSitePalette

private fun getUrlParam(name: String): String? {
    val params = js("new URLSearchParams(window.location.search)")
    return params.get(name) as? String
}

@Page("/chat")
@Composable
fun ChatPage() {
    val apiKeyFromUrl = remember { getUrlParam("apiKey") }
    val personaIdFromUrl = remember { getUrlParam("personaId")?.toIntOrNull() }

    val chatCredsViewModel = remember { ChatCredentialsViewModel() }
    val credentials by chatCredsViewModel.credentials.collectAsState()
    val loading by chatCredsViewModel.loading.collectAsState()

    val palette = ColorMode.current.toSitePalette()

    val chatModifier = Modifier
        .fillMaxSize()
        .width(100.vw)
        .minHeight(100.vh)
        .background(palette.nearBackground)

    when {
        apiKeyFromUrl != null && personaIdFromUrl != null -> ChatBlock(
            modifier = chatModifier,
            personaId = personaIdFromUrl,
            apiKey = apiKeyFromUrl,
        )
    }
    credentials?.let {
        ChatBlock(
            modifier = chatModifier,
            personaId = it.personaId,
            apiKey = it.apiKey,
        )
    }
}
