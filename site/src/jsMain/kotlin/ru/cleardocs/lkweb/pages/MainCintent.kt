package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.boxShadow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.dom.Div
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import kotlinx.browser.document
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.accept
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.multiple
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Input
import ru.cleardocs.lkweb.components.layouts.PageLayout
import ru.cleardocs.lkweb.components.widgets.ActionButton
import ru.cleardocs.lkweb.components.widgets.AuthInput
import ru.cleardocs.lkweb.components.widgets.InputLayout
import ru.cleardocs.lkweb.components.widgets.Toast
import ru.cleardocs.lkweb.components.widgets.authInputStyle
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.connectors.toByteArray
import ru.cleardocs.lkweb.chat.ChatCredentialsViewModel
import ru.cleardocs.lkweb.components.sections.ChatBlock
import ru.cleardocs.lkweb.components.sections.ProfileBlock
import ru.cleardocs.lkweb.components.sections.ProfileMenu
import ru.cleardocs.lkweb.firebase.signOut
import ru.cleardocs.lkweb.connectors.Connector
import ru.cleardocs.lkweb.connectors.ConnectorType
import ru.cleardocs.lkweb.connectors.ConnectorsViewState
import ru.cleardocs.lkweb.connectors.ConnectorsViewState.ConnectorsData
import ru.cleardocs.lkweb.connectors.ConnectorsViewModel
import ru.cleardocs.lkweb.plans.Plans
import ru.cleardocs.lkweb.profile.MeViewModel
import ru.cleardocs.lkweb.profile.ProfileAuthState
import ru.cleardocs.lkweb.toSitePalette
import ru.cleardocs.lkweb.utils.requireProfileAuthRedirect
import ru.cleardocs.lkweb.ActionButtonVariant
import ru.cleardocs.lkweb.di.kodein
import ru.cleardocs.lkweb.pages.MenuViewModel
import org.kodein.di.instance
import kotlinx.browser.window

@Composable
private fun MainContent(mainState: MainViewState, meViewModel: MeViewModel) {
    when (mainState) {
        MainViewState.Profile -> ProfileContent(meViewModel = meViewModel)
        MainViewState.Connectors -> ConnectorsContent()
        MainViewState.Plans -> PlansContent(meViewModel = meViewModel)
    }
}

@Composable
private fun PlansContent(meViewModel: MeViewModel) {
    val me by meViewModel.me.collectAsState()
    val currentPlanCode = me?.plan?.code
    val palette = ColorMode.current.toSitePalette()

    Column(
        Modifier
            .flexGrow(1)
            .fillMaxSize()
            .gap(1.25.cssRem)
            .cardSurface(palette),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Plans(currentPlanCode = currentPlanCode)
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

/**
 * Маппинг статусов коннектора по Onyx ConnectorCredentialPairStatus
 * (Swagger: /api/openapi.json → ConnectorCredentialPairStatus)
 * Enum: SCHEDULED, INITIAL_INDEXING, ACTIVE, PAUSED, DELETING, INVALID
 */
private fun connectorStatusLabel(status: String?): String = when (status?.uppercase()) {
    "ACTIVE" -> "Готов"
    "SCHEDULED", "INITIAL_INDEXING" -> "Индексация"
    "INVALID" -> "Ошибка"
    "PAUSED" -> "Приостановлен"
    "DELETING" -> "Удаление"
    null, "" -> "—"
    else -> status
}

private fun connectorStatusColors(status: String?): Pair<Color, Color> = when (status?.uppercase()) {
    "ACTIVE" -> Color.rgb(0x22C55E) to Colors.White
    "SCHEDULED", "INITIAL_INDEXING" -> Color.rgb(0xF59E0B) to Color.rgb(0x1F2937)
    "INVALID" -> Color.rgb(0xDC2626) to Colors.White
    "PAUSED" -> Color.rgb(0x94A3B8) to Color.rgb(0x1E293B)
    "DELETING" -> Color.rgb(0x64748B) to Colors.White
    else -> Color.rgb(0x94A3B8) to Color.rgb(0x1E293B)
}

@Composable
private fun ConnectorStatusBadge(status: String?, modifier: Modifier = Modifier) {
    val label = connectorStatusLabel(status)
    val (bg, text) = connectorStatusColors(status)
    Span(
        modifier
            .borderRadius(0.35.cssRem)
            .padding(left = 0.45.cssRem, right = 0.45.cssRem, top = 0.2.cssRem, bottom = 0.2.cssRem)
            .fontSize(0.75.cssRem)
            .toAttrs {
                style {
                    property("background", bg.toString())
                    property("color", text.toString())
                    property("font-weight", "500")
                    property("white-space", "nowrap")
                }
            }
    ) {
        SpanText(label)
    }
}

@Composable
private fun ConnectorStatusButtons(
    connectorId: String,
    statusUpper: String?,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    if (statusUpper == "ACTIVE") {
        Button(
            onClick = { onPause(connectorId) },
            modifier = Modifier.fontSize(0.8.cssRem).padding(0.2.cssRem)
        ) {
            SpanText("Пауза")
        }
    }
    if (statusUpper == "PAUSED") {
        Button(
            onClick = { onResume(connectorId) },
            modifier = Modifier.fontSize(0.8.cssRem).padding(0.2.cssRem)
        ) {
            SpanText("Возобновить")
        }
        Button(
            onClick = { onDelete(connectorId) },
            modifier = Modifier.fontSize(0.8.cssRem).padding(0.2.cssRem)
        ) {
            SpanText("Удалить")
        }
    }
}

@Composable
private fun ConnectorItem(
    connector: Connector,
    palette: ru.cleardocs.lkweb.SitePalette,
    onDelete: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
) {
    val textColor = ColorMode.current.toPalette().color
    val statusUpper = connector.status?.uppercase()
    val iconSrc = when (connector.type.uppercase()) {
        "URL" -> "/globe-icon.svg"
        else -> "/file-icon.svg"
    }
    val itemBg = when (ColorMode.current) {
        ColorMode.LIGHT -> Colors.White
        ColorMode.DARK -> palette.nearBackground
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(1.25.cssRem)
            .borderRadius(0.75.cssRem)
            .backgroundColor(itemBg)
            .boxShadow(2.px, 2.px, 8.px, color = palette.brand.primary.toRgb().copyf(alpha = 0.12f))
            .gap(0.75.cssRem),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Img(
            src = iconSrc,
            alt = connector.name,
            attrs = {
                style {
                    property("width", "2rem")
                    property("height", "2rem")
                    property("object-fit", "contain")
                    property("flex-shrink", "0")
                }
            }
        )
        Div(
            attrs = {
                style {
                    property("flex-grow", "1")
                    property("overflow", "hidden")
                    property("text-overflow", "ellipsis")
                    property("min-width", "0")
                }
            }
        ) {
            SpanText(connector.name, Modifier.color(textColor).fontSize(1.cssRem))
        }
        ConnectorStatusBadge(connector.status, Modifier.flexShrink(0))
        ConnectorStatusButtons(
            connectorId = connector.id,
            statusUpper = statusUpper,
            onPause = onPause,
            onResume = onResume,
            onDelete = onDelete,
        )
    }
}

@Composable
private fun ConnectorsList(
    connectors: List<Connector>,
    onDelete: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
) {
    val palette = ColorMode.current.toSitePalette()
    Column(
        Modifier
            .fillMaxWidth()
            .gap(1.25.cssRem)
    ) {
        connectors.forEach { c ->
            ConnectorItem(
                connector = c,
                palette = palette,
                onDelete = onDelete,
                onPause = onPause,
                onResume = onResume,
            )
        }
    }
}

@Composable
private fun NoConnectorsMessage() {
    SpanText("Нет коннекторов.")
}

@Composable
private fun ConnectorTypeCard(
    iconSrc: String,
    label: String,
    palette: ru.cleardocs.lkweb.SitePalette,
    onClick: () -> Unit
) {
    val textColor = ColorMode.current.toPalette().color
    val cardBg = when (ColorMode.current) {
        ColorMode.LIGHT -> "white"
        ColorMode.DARK -> palette.nearBackground.toString()
    }
    Div(
        attrs = {
            style {
                property("cursor", "pointer")
                property("display", "flex")
                property("flex-direction", "column")
                property("align-items", "center")
                property("justify-content", "center")
                property("gap", "0.75rem")
                property("padding", "1.25rem")
                property("width", "7rem")
                property("min-height", "6rem")
                property("border-radius", "0.75rem")
                property("background", cardBg)
                property("box-shadow", "2px 2px 8px ${palette.brand.primary.toRgb().copyf(alpha = 0.12f)}")
                property("transition", "box-shadow 0.2s ease")
            }
            onClick { onClick() }
        }
    ) {
        Img(
            src = iconSrc,
            alt = label,
            attrs = {
                style {
                    property("width", "2rem")
                    property("height", "2rem")
                    property("object-fit", "contain")
                }
            }
        )
        SpanText(label, Modifier.color(textColor).fontSize(1.cssRem))
    }
}

@Composable
private fun AddConnectorBlock(
    connectorsViewModel: ConnectorsViewModel,
    onBack: () -> Unit,
    palette: ru.cleardocs.lkweb.SitePalette
) {
    var selectedType by remember { mutableStateOf<ConnectorType?>(null) }

    Column(
        Modifier
            .fillMaxWidth()
            .gap(1.cssRem)
    ) {
        Row(
            Modifier.fillMaxWidth().gap(0.5.cssRem),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                text = "Назад",
                onClick = {
                    if (selectedType == null) {
                        onBack()
                    } else {
                        selectedType = null
                    }
                },
                enabled = true
            )
        }

        when (selectedType) {
            null -> {
                SpanText("Выберите тип коннектора", Modifier.fontSize(1.1.cssRem))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .gap(1.25.cssRem),
                    horizontalArrangement = Arrangement.Start
                ) {
                    ConnectorTypeCard(
                        iconSrc = "/globe-icon.svg",
                        label = "Web",
                        palette = palette,
                        onClick = { selectedType = ConnectorType.Url }
                    )
                    ConnectorTypeCard(
                        iconSrc = "/file-icon.svg",
                        label = "Файл",
                        palette = palette,
                        onClick = { selectedType = ConnectorType.File }
                    )
                }
            }
            ConnectorType.File -> AddFileConnectorForm(
                connectorsViewModel = connectorsViewModel,
                palette = palette
            )
            ConnectorType.Url -> AddUrlConnectorForm(
                connectorsViewModel = connectorsViewModel,
                palette = palette
            )
        }
    }
}

@Composable
private fun AddFileConnectorForm(
    connectorsViewModel: ConnectorsViewModel,
    palette: ru.cleardocs.lkweb.SitePalette
) {
    var connectorName by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val colorPalette = ColorMode.current.toPalette()
    val inputBg = colorPalette.background.toString()
    val inputFg = colorPalette.color.toString()
    val inputBorder = palette.cobweb.toString()

    Column(
        Modifier
            .fillMaxWidth()
            .gap(1.cssRem)
    ) {
        SpanText("Добавить файловый коннектор", Modifier.fontSize(1.1.cssRem))

        InputLayout(label = "Название") {
            AuthInput(
                type = InputType.Text,
                value = connectorName,
                placeholder = "Введите название коннектора",
                onValueChange = { connectorName = it },
                inputBg = inputBg,
                inputFg = inputFg,
                inputBorder = inputBorder,
                enabled = !isAdding,
                marginBottom = null
            )
        }

        InputLayout(label = "Файлы") {
            Input(
                type = InputType.File,
                attrs = {
                    id("connector-file-input")
                    accept(".txt,.docx,.pptx,.xlsx,.csv,.eml,.epub,.zip,.pdf")
                    multiple()
                    if (isAdding) disabled()
                    style(authInputStyle(inputBg, inputFg, inputBorder, null))
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
                        if (e is kotlinx.coroutines.CancellationException) throw e
                        isAdding = false
                    }
                }
            },
            enabled = !isAdding
        )
    }
}

@Composable
private fun AddUrlConnectorForm(
    connectorsViewModel: ConnectorsViewModel,
    palette: ru.cleardocs.lkweb.SitePalette
) {
    var connectorName by remember { mutableStateOf("") }
    var connectorUrl by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }
    val colorPalette = ColorMode.current.toPalette()
    val inputBg = colorPalette.background.toString()
    val inputFg = colorPalette.color.toString()
    val inputBorder = palette.cobweb.toString()

    Column(
        Modifier
            .fillMaxWidth()
            .gap(1.cssRem)
    ) {
        SpanText("Добавить URL-коннектор", Modifier.fontSize(1.1.cssRem))

        InputLayout(label = "Название") {
            AuthInput(
                type = InputType.Text,
                value = connectorName,
                placeholder = "Введите название коннектора",
                onValueChange = { connectorName = it },
                inputBg = inputBg,
                inputFg = inputFg,
                inputBorder = inputBorder,
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
                inputBg = inputBg,
                inputFg = inputFg,
                inputBorder = inputBorder,
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
        when (val s = state) {
            is ConnectorsViewState.Loading -> {}
            is ConnectorsViewState.GotoAuth ->
                SpanText("Перенаправляем на авторизацию...")
            is ConnectorsViewState.Error ->
                SpanText("Ошибка: ${s.message}")
            is ConnectorsData.Connectors -> {
                DisposableEffect(Unit) {
                    onDispose { connectorsViewModel.stopPolling() }
                }
                SpanText("Коннекторы", Modifier.fontSize(1.5.cssRem))
                if (s.connectors.isEmpty()) {
                    NoConnectorsMessage()
                } else {
                    ConnectorsList(
                        connectors = s.connectors,
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
                    if (s.canAdd) {
                        ActionButton(
                            text = "Добавить коннектор",
                            onClick = { connectorsViewModel.goToAddConnector() },
                        )
                    }
                }
            }
            is ConnectorsData.AddConnector -> Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 1.5.cssRem)
                    .borderRadius(0.6.cssRem)
                    .padding(1.cssRem)
                    .backgroundColor(palette.nearBackground)
            ) {
                AddConnectorBlock(
                    connectorsViewModel = connectorsViewModel,
                    onBack = { connectorsViewModel.backToConnectors() },
                    palette = palette
                )
            }
            is ConnectorsData.Chat -> Column(
                Modifier
                    .fillMaxWidth()
                    .flexGrow(1)
                    .padding(top = 1.5.cssRem)
                    .borderRadius(0.6.cssRem)
                    .gap(1.cssRem)
                    .backgroundColor(palette.nearBackground)
            ) {
                val chatCredsViewModel = remember { ChatCredentialsViewModel() }
                val credentials by chatCredsViewModel.credentials.collectAsState()
                var toastMessage by remember { mutableStateOf<String?>(null) }
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
                                    .then {
                                        toastMessage = "Скопировано в буфер обмена"
                                        window.setTimeout({ toastMessage = null }, 2500)
                                    }
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
                toastMessage?.let { msg ->
                    Toast(message = msg)
                }
                val error by chatCredsViewModel.error.collectAsState()
                when {
                    credentials != null -> ChatBlock(
                        modifier = Modifier.flexGrow(1),
                        personaId = credentials!!.personaId,
                        apiKey = credentials!!.apiKey,
                    )
                    else -> SpanText("Ошибка: ${error ?: "Не удалось загрузить credentials"}")
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
