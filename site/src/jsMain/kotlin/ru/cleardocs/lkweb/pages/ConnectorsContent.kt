package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.css.cssRem
import kotlin.js.console
import kotlinx.browser.window
import ru.cleardocs.lkweb.components.widgets.ActionButton
import ru.cleardocs.lkweb.components.widgets.rememberTimedToast
import ru.cleardocs.lkweb.connectors.ConnectorType
import ru.cleardocs.lkweb.connectors.ConnectorTypeCardsRow
import ru.cleardocs.lkweb.connectors.ConnectorsList
import ru.cleardocs.lkweb.connectors.ConnectorsViewState
import ru.cleardocs.lkweb.connectors.ConnectorsViewState.ConnectorsData
import ru.cleardocs.lkweb.connectors.ConnectorsViewModel
import ru.cleardocs.lkweb.connectors.NoConnectorsMessage
import ru.cleardocs.lkweb.chat.ChatCredentialsViewModel
import ru.cleardocs.lkweb.components.sections.ChatBlock
import ru.cleardocs.lkweb.ActionButtonVariant
import ru.cleardocs.lkweb.SiteTokens
import ru.cleardocs.lkweb.firebase.firebaseLog
import ru.cleardocs.lkweb.toSitePalette

@Composable
internal fun ConnectorsContent() {
    val connectorsViewModel = remember { ConnectorsViewModel() }
    val state by connectorsViewModel.state.collectAsState()
    val palette = ColorMode.current.toSitePalette()

    Column(
        Modifier
            .flexGrow(1)
            .fillMaxSize()
            .gap(SiteTokens.Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val s = state) {
            is ConnectorsViewState.Loading -> {}
            is ConnectorsViewState.GotoAuth -> {
                firebaseLog("Nav", "GotoAuth", "reason= ConnectorsViewModel 401", "redirect= no")
                SpanText("Перенаправляем на авторизацию...")
            }

            is ConnectorsViewState.Error ->
                SpanText("Ошибка: ${s.message}")

            is ConnectorsData.Connectors -> {
                var selectedType by remember { mutableStateOf<ConnectorType?>(null) }
                DisposableEffect(Unit) {
                    onDispose { connectorsViewModel.stopPolling() }
                }
                SpanText("Коннекторы", Modifier.fontSize(1.5.cssRem))
                if (s.connectors.isEmpty()) {
                    NoConnectorsMessage()
                } else {
                    ConnectorsList(
                        connectors = s.connectors,
                        onClick = { connectorsViewModel.goToChat() },
                        onDelete = { id -> connectorsViewModel.deleteConnector(id) },
                        onPause = { id -> connectorsViewModel.setConnectorStatus(id, "PAUSED") },
                        onResume = { id -> connectorsViewModel.setConnectorStatus(id, "ACTIVE") },
                    )
                }
                Row(Modifier.gap(1.cssRem)) {
                    ActionButton(
                        text = "Перейти в чат",
                        onClick = { connectorsViewModel.goToChat() },
                        enabled = s.connectors.isNotEmpty()
                    )
                }
                ConnectorTypeCardsRow(
                    palette = palette,
                    canAdd = s.canAdd,
                    selectedType = selectedType,
                    onSelectType = { selectedType = it },
                    connectorsViewModel = connectorsViewModel
                )
            }

            is ConnectorsData.Chat -> Column(
                Modifier
                    .fillMaxWidth()
                    .flexGrow(1)
                    .padding(top = 1.5.cssRem)
                    .borderRadius(0.6.cssRem)
                    .gap(1.cssRem)
            ) {
                val chatCredsViewModel = remember { ChatCredentialsViewModel() }
                val credentials by chatCredsViewModel.credentials.collectAsState()
                val showToast = rememberTimedToast()
                Row(
                    Modifier.fillMaxWidth().gap(0.5.cssRem),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButton(
                        text = "Назад",
                        onClick = { connectorsViewModel.backFromChat() },
                    )
                    Box(Modifier.flexGrow(1))
                    credentials?.let {
                        val shareUrl =
                            "https://lk.cleardocs.ru/chat?apiKey=${it.apiKey}&personaId=${it.personaId}"
                        ActionButton(
                            text = "Поделиться",
                            onClick = {
                                window.navigator.clipboard.writeText(shareUrl)
                                    .then { showToast("Скопировано в буфер обмена") }
                                    .catch {
                                        console.error("Не удалось скопировать в буфер обмена", it)
                                    }
                            }
                        )
                        Button(
                            onClick = { window.open(shareUrl, "_blank") },
                            variant = ActionButtonVariant
                        ) {
                            Img(
                                src = "/run-above.svg",
                                alt = "Открыть в новой вкладке"
                            ) {
                                style {
                                    property("width", "1.25rem")
                                    property("height", "1.25rem")
                                }
                            }
                        }
                    }
                }
                credentials?.let {
                    ChatBlock(
                        modifier = Modifier.flexGrow(1),
                        personaId = it.personaId,
                        apiKey = it.apiKey,
                    )
                }
            }
        }
    }
}
