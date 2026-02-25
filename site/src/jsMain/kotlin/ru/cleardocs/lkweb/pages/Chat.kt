package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.chat.ChatCredentialsViewModel
import ru.cleardocs.lkweb.components.sections.ChatBlock

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
    val error by chatCredsViewModel.error.collectAsState()

    when {
        apiKeyFromUrl != null && personaIdFromUrl != null -> ChatBlock(
            modifier = Modifier.fillMaxSize(),
            personaId = personaIdFromUrl,
            apiKey = apiKeyFromUrl,
        )

        loading -> Box(Modifier.fillMaxSize().fillMaxWidth()) {
            SpanText("Загрузка чата...", modifier = Modifier.padding(2.cssRem))
        }
    }
    credentials?.let {
        ChatBlock(
            modifier = Modifier.fillMaxSize(),
            personaId = it.personaId,
            apiKey = it.apiKey,
        )
    }
}
