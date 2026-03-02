package ru.cleardocs.lkweb.connectors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import kotlinx.browser.document
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.accept
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.multiple
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Input
import ru.cleardocs.lkweb.components.widgets.ActionButton
import ru.cleardocs.lkweb.components.widgets.AuthInput
import ru.cleardocs.lkweb.components.widgets.InputLayout
import ru.cleardocs.lkweb.components.widgets.authInputStyle
import ru.cleardocs.lkweb.connectors.toByteArray
import com.varabyte.kobweb.silk.components.text.SpanText
import ru.cleardocs.lkweb.rememberInputColors
import ru.cleardocs.lkweb.SitePalette
import ru.cleardocs.lkweb.SiteTokens
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun AddFileConnectorForm(
    connectorsViewModel: ConnectorsViewModel,
    palette: SitePalette, // kept for API consistency with ConnectorTypeCardsRow
) {
    var connectorName by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val inputColors = rememberInputColors()

    Column(
        Modifier
            .fillMaxWidth()
            .gap(SiteTokens.Spacing.lg)
    ) {
        SpanText("Добавить файловый коннектор", Modifier.fontSize(1.1.cssRem))

        InputLayout(label = "Название") {
            AuthInput(
                type = InputType.Text,
                value = connectorName,
                placeholder = "Введите название коннектора",
                onValueChange = { connectorName = it },
                inputBg = inputColors.background,
                inputFg = inputColors.foreground,
                inputBorder = inputColors.border,
                enabled = !isAdding,
                marginBottom = null
            )
        }

        InputLayout(label = "Файлы") {
            SpanText(
                "Форматы: txt, md, csv, eml, docx, pptx, xlsx, epub, pdf, lic, json, org",
                Modifier.fontSize(0.8.cssRem).padding(bottom = 0.25.cssRem).color(ColorMode.current.toPalette().color.toRgb().copyf(alpha = 0.65f))
            )
            Input(
                type = InputType.File,
                attrs = {
                    id("connector-file-input")
                    accept(".txt,.md,.csv,.eml,.docx,.pptx,.xlsx,.epub,.pdf,.lic,.json,.org")
                    multiple()
                    if (isAdding) disabled()
                    style(authInputStyle(inputColors.background, inputColors.foreground, inputColors.border, null))
                }
            )
        }

        ActionButton(
            text = if (isAdding) "Добавление..." else "Добавить",
            onClick = {
                val name = connectorName.trim()
                if (name.isEmpty()) return@ActionButton
                val input = document.getElementById("connector-file-input")
                    ?.unsafeCast<org.w3c.dom.HTMLInputElement>()
                val files = input?.files
                if (files == null || files.length == 0) return@ActionButton

                isAdding = true
                scope.launch {
                    try {
                        val byteArrays = mutableListOf<ByteArray>()
                        val filenames = mutableListOf<String>()
                        for (i in 0 until files.length) {
                            val file = files.item(i) as? org.w3c.files.File ?: continue
                            byteArrays.add(file.toByteArray())
                            filenames.add(file.name)
                        }
                        if (byteArrays.isNotEmpty()) {
                            connectorsViewModel.addConnector(name, byteArrays, filenames) { isAdding = false }
                            connectorName = ""
                            input.let { it.value = "" }
                        } else {
                            isAdding = false
                        }
                    } catch (e: Throwable) {
                        if (e is CancellationException) throw e
                        isAdding = false
                    }
                }
            },
            enabled = !isAdding
        )

        SpanText(
            "Загрузите файлы, выберите путь.",
            Modifier.fontSize(0.85.cssRem).color(palette.brand.primary)
        )
    }
}

@Composable
fun AddUrlConnectorForm(
    connectorsViewModel: ConnectorsViewModel,
    palette: SitePalette, // kept for API consistency with ConnectorTypeCardsRow
) {
    var connectorName by remember { mutableStateOf("") }
    var connectorUrl by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }
    val inputColors = rememberInputColors()

    Column(
        Modifier
            .fillMaxWidth()
            .gap(SiteTokens.Spacing.lg)
    ) {
        SpanText("Добавить URL-коннектор", Modifier.fontSize(1.1.cssRem))

        InputLayout(label = "Название") {
            AuthInput(
                type = InputType.Text,
                value = connectorName,
                placeholder = "Введите название коннектора",
                onValueChange = { connectorName = it },
                inputBg = inputColors.background,
                inputFg = inputColors.foreground,
                inputBorder = inputColors.border,
                enabled = !isAdding,
                marginBottom = null
            )
        }

        InputLayout(label = "URL") {
            AuthInput(
                type = InputType.Text,
                value = connectorUrl,
                placeholder = "https://example.com",
                onValueChange = { connectorUrl = it },
                inputBg = inputColors.background,
                inputFg = inputColors.foreground,
                inputBorder = inputColors.border,
                enabled = !isAdding,
                marginBottom = null
            )
        }

        ActionButton(
            text = if (isAdding) "Добавление..." else "Добавить",
            onClick = {
                val name = connectorName.trim()
                val url = connectorUrl.trim()
                if (name.isEmpty() || url.isEmpty()) return@ActionButton

                isAdding = true
                connectorsViewModel.addUrlConnector(name, url, recursive = true) {
                    isAdding = false
                    connectorName = ""
                    connectorUrl = ""
                }
            },
            enabled = !isAdding
        )

        SpanText(
            "Подключите любой сайт рекурсивно, укажите url сайта.",
            Modifier.fontSize(0.85.cssRem).color(palette.brand.primary)
        )
    }
}
