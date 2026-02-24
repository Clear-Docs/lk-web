package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.accept
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.multiple
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import ru.cleardocs.lkweb.components.layouts.PageLayout
import ru.cleardocs.lkweb.components.widgets.AuthInput
import ru.cleardocs.lkweb.components.widgets.InputLayout
import ru.cleardocs.lkweb.components.widgets.authInputStyle
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.connectors.toByteArray
import ru.cleardocs.lkweb.components.sections.ProfileBlock
import ru.cleardocs.lkweb.components.sections.ProfileMenu
import ru.cleardocs.lkweb.firebase.signOut
import ru.cleardocs.lkweb.connectors.Connector
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
private fun ConnectorItem(connector: Connector, palette: ru.cleardocs.lkweb.SitePalette) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(topBottom = 0.5.cssRem, leftRight = 0.75.cssRem)
            .borderRadius(0.5.cssRem)
            .backgroundColor(palette.nearBackground)
            .gap(0.5.cssRem),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Img(
            src = "/file.svg",
            alt = "",
            attrs = {
                style {
                    property("width", "1.125rem")
                    property("height", "1.125rem")
                    property("flex-shrink", "0")
                }
            }
        )
        SpanText(connector.name)
    }
}

@Composable
private fun ConnectorsList(connectors: List<Connector>) {
    val palette = ColorMode.current.toSitePalette()
    Column(Modifier.fillMaxWidth().gap(0.5.cssRem)) {
        connectors.forEach { c -> ConnectorItem(c, palette) }
    }
}

@Composable
private fun NoConnectorsMessage() {
    SpanText("Нет коннекторов.")
}

@Composable
private fun ConnectorsContent() {
    val connectorsViewModel = remember { ConnectorsViewModel() }
    val state by connectorsViewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val palette = ColorMode.current.toSitePalette()
    val colorPalette = ColorMode.current.toPalette()

    var connectorName by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }

    val inputBg = colorPalette.background.toString()
    val inputFg = colorPalette.color.toString()
    val inputBorder = palette.cobweb.toString()

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
                    NoConnectorsMessage()
                } else {
                    ConnectorsList(connectors)
                }
            }
        }

        if (state is ConnectorsViewState.ConnectorsData || state is ConnectorsViewState.Error) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .gap(1.cssRem)
                    .padding(top = 1.5.cssRem)
                    .borderRadius(0.6.cssRem)
                    .padding(1.cssRem)
                    .backgroundColor(palette.nearBackground)
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

                Button(
                    onClick = {
                        val name = connectorName.trim()
                        if (name.isEmpty()) return@Button
                        val input = document.getElementById("connector-file-input")
                            ?.unsafeCast<org.w3c.dom.HTMLInputElement>()
                        val files = input?.files
                        if (files == null || files.length == 0) return@Button

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
                                    connectorsViewModel.addConnector(name, byteArrays, filenames)
                                    connectorName = ""
                                    input?.let { it.value = "" }
                                }
                            } finally {
                                isAdding = false
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 0.25.cssRem),
                    enabled = !isAdding
                ) {
                    SpanText(if (isAdding) "Добавление..." else "Добавить")
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
