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
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayUntil
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
import ru.cleardocs.lkweb.components.widgets.ContentCard
import ru.cleardocs.lkweb.components.widgets.rememberTimedToast
import ru.cleardocs.lkweb.connectors.AddFileConnectorForm
import ru.cleardocs.lkweb.connectors.AddUrlConnectorForm
import ru.cleardocs.lkweb.connectors.ConnectorTypeCardsRow
import ru.cleardocs.lkweb.connectors.ConnectorsList
import ru.cleardocs.lkweb.connectors.NoConnectorsMessage
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
import ru.cleardocs.lkweb.SiteTokens
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

    Column(
        Modifier
            .flexGrow(1)
            .fillMaxSize()
            .gap(SiteTokens.Spacing.xl),
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

    val profileBody: @Composable () -> Unit = {
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

    Box(Modifier.flexGrow(1).fillMaxSize()) {
        Column(
            Modifier
                .displayUntil(Breakpoint.MD)
                .flexGrow(1)
                .fillMaxSize()
                .gap(SiteTokens.Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            profileBody()
        }
        ContentCard(
            palette = palette,
            modifier = Modifier.displayIfAtLeast(Breakpoint.MD)
        ) {
            profileBody()
        }
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
            .gap(SiteTokens.Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val s = state) {
            is ConnectorsViewState.Loading -> {}
            is ConnectorsViewState.GotoAuth ->
                SpanText("Перенаправляем на авторизацию...")

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
