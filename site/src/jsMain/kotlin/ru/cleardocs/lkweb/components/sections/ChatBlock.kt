package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.setVariable
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.percent
import ru.cleardocs.lkweb.chat.ChatMessage
import ru.cleardocs.lkweb.chat.ChatRole
import ru.cleardocs.lkweb.chat.ChatViewModel
import ru.cleardocs.lkweb.components.widgets.ExpandableChatInput
import ru.cleardocs.lkweb.toSitePalette

/**
 * Чат с стриминговой подпиской через Flow.
 * Использует [ChatViewModel] и [ChatApi.sendMessageStream].
 */
@Composable
fun ChatBlock(
    modifier: Modifier = Modifier,
    personaId: Int = 0,
    apiKey: String? = null,
) {
    val viewModel = remember { ChatViewModel(personaId = personaId, apiKey = apiKey) }
    val messages by viewModel.messages.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val palette = ColorMode.current.toSitePalette()
    val (inputBg, inputFg) = when (ColorMode.current) {
        ColorMode.LIGHT -> "#FFFFFF" to "#0F172A"
        ColorMode.DARK -> "#0B1120" to "#FFFFFF"
    }
    val inputBorder = palette.cobweb.toString()

    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(24.cssRem)
    ) {
        // Список сообщений — MutationObserver для автоскролла при изменении контента
        val scrollRef = remember { mutableStateOf<org.w3c.dom.HTMLElement?>(null) }
        Div(
            Modifier
                .flexGrow(1)
                .fillMaxWidth()
                .minHeight(0.px)
                .padding(0.5.cssRem)
                .backgroundColor(palette.nearBackground)
                .overflow(Overflow.Auto)
                .toAttrs {
                    ref { el ->
                        val element = el.unsafeCast<org.w3c.dom.HTMLElement>()
                        scrollRef.value = element
                        val observer = org.w3c.dom.MutationObserver { _, _ ->
                            kotlinx.browser.window.requestAnimationFrame {
                                element.scrollTop = element.scrollHeight.toDouble()
                            }
                        }
                        observer.observe(
                            element,
                            js("({ childList: true, subtree: true, characterData: true })")
                        )
                        onDispose {
                            observer.disconnect()
                            scrollRef.value = null
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().gap(0.75.cssRem),
                verticalArrangement = Arrangement.Top
            ) {
                messages.forEach { msg ->
                    ChatBubble(
                        message = msg,
                        palette = palette,
                        isUser = msg.role == ChatRole.USER,
                    )
                }
            }
        }

        // Ошибка
        if (error != null) {
            SpanText(
                "Ошибка: $error",
                Modifier
                    .fillMaxWidth()
                    .padding(0.25.cssRem)
                    .color(Color.rgb(0xDC2626))
            )
        }

        // Поле ввода и кнопка
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .gap(0.5.cssRem)
                .backgroundColor(palette.nearBackground)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().gap(0.5.cssRem),
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(Modifier.flexGrow(1)) {
                    ExpandableChatInput(
                        value = inputText,
                        placeholder = "Введите сообщение...",
                        onValueChange = { inputText = it },
                        inputBg = inputBg,
                        inputFg = inputFg,
                        inputBorder = inputBorder,
                        enabled = !loading,
                        marginBottom = null,
                        onEnterSubmit = {
                            if (inputText.isNotBlank() && !loading) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        }
                    )
                }
                val canSend = inputText.isNotBlank() && !loading
                Button(
                    onClick = {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    },
                    modifier = Modifier
                        .padding(0.5.cssRem)
                        .then(
                            if (canSend) Modifier
                                .setVariable(ButtonVars.BackgroundDefaultColor, palette.brand.primary)
                                .color(Colors.White)
                            else Modifier
                                .setVariable(ButtonVars.BackgroundDefaultColor, palette.cobweb)
                        ),
                    enabled = canSend
                ) {
                    Img(
                        src = if (canSend) "/send-active.svg" else "/send.svg",
                        alt = "Отправить"
                    ) {
                        style {
                            property("width", "1.25rem")
                            property("height", "1.25rem")
                        }
                    }
                }
                Tooltip(ElementTarget.PreviousSibling, "Отправить", placement = PopupPlacement.Bottom)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top =0.5.cssRem),
                horizontalArrangement = Arrangement.Center
            ) {
                Link(
                    "#",
                    modifier = Modifier
                        .fontSize(0.85.cssRem)
                        .onClick { it.preventDefault(); viewModel.resetConversation() },
                    variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
                ) {
                    SpanText("Сбросить")
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    palette: ru.cleardocs.lkweb.SitePalette,
    isUser: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .width(if (isUser) 70.percent else 80.percent)
                .backgroundColor(if (isUser) palette.brand.primary.toRgb().copyf(alpha = 0.15f) else palette.cobweb)
                .padding(0.75.cssRem)
                .borderRadius(0.6.cssRem)
        ) {
            Column {
                SpanText(
                    if (message.content.isNotEmpty()) message.content else if (message.isLoading) "..." else "",
                    Modifier.fillMaxWidth()
                )
            }
        }
    }
}
