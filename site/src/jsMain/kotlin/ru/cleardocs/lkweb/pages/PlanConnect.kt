package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.boxShadow
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.setVariable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import ru.cleardocs.lkweb.UncoloredButtonVariant
import ru.cleardocs.lkweb.api.BackendApi
import ru.cleardocs.lkweb.components.layouts.PageLayout
import ru.cleardocs.lkweb.toSitePalette
import ru.cleardocs.lkweb.utils.toUserFriendlyMessage

private fun getUrlParam(name: String): String? {
    val params = js("new URLSearchParams(window.location.search)")
    return params.get(name) as? String
}

@Page("/plans/pay")
@Composable
fun PlanConnectPage() {
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()
    val planCode = remember { getUrlParam("plan") }
    var showConfirmDialog by remember { mutableStateOf(planCode != null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val palette = ColorMode.current.toSitePalette()

    fun startPayment() {
        if (planCode == null) return
        showConfirmDialog = false
        loading = true
        error = null
        scope.launch {
            try {
                val response = BackendApi.createTochkaPayment(planCode)
                window.location.href = response.paymentUrl
            } catch (e: Throwable) {
                error = e.toUserFriendlyMessage("Ошибка создания платежа")
                loading = false
            }
        }
    }

    PageLayout("Подключение тарифа") {
        Box(
            Modifier
                .fillMaxSize()
                .padding(1.cssRem),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.gap(1.5.cssRem),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    showConfirmDialog && planCode != null -> {
                        // Диалог показывается в отдельном Box поверх страницы ниже
                    }
                    loading -> SpanText("Перенаправление к оплате…", Modifier.fontSize(1.25.cssRem))
                    error != null -> {
                        SpanText("Ошибка: $error", Modifier.fontSize(1.25.cssRem))
                        Button(onClick = { ctx.router.tryRoutingTo("/") }) {
                            Text("Вернуться в личный кабинет")
                        }
                    }
                    planCode == null -> {
                        SpanText("Укажите тариф", Modifier.fontSize(1.25.cssRem))
                        Button(onClick = { ctx.router.tryRoutingTo("/") }) {
                            Text("Вернуться в личный кабинет")
                        }
                    }
                }
            }

            if (showConfirmDialog && planCode != null) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(0.px)
                        .then(Modifier.position(Position.Fixed).left(0.px).top(0.px).zIndex(200))
                        .backgroundColor(Colors.Black.toRgb().copyf(alpha = 0.4f))
                        .onClick { showConfirmDialog = false },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .maxWidth(32.cssRem)
                            .padding(1.5.cssRem)
                            .borderRadius(1.cssRem)
                            .backgroundColor(palette.nearBackground)
                            .border(1.px, LineStyle.Solid, palette.cobweb)
                            .boxShadow(4.px, 4.px, 12.px, color = Colors.Black.toRgb().copyf(alpha = 0.2f))
                            .onClick { it.stopPropagation() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(Modifier.gap(1.cssRem).padding(1.cssRem)) {
                            SpanText(
                                "Вы оформляете подписку на месяц с автоматическим продлением. Но вы можете отменить её в любой момент с сохранением услуг до окончания периода.",
                                Modifier.fontSize(1.05.cssRem)
                            )
                            Row(Modifier.gap(0.75.cssRem)) {
                                Button(
                                    onClick = { startPayment() },
                                    Modifier.setVariable(ButtonVars.FontSize, 1.em)
                                ) {
                                    Text("Оформить")
                                }
                                Button(
                                    onClick = { ctx.router.tryRoutingTo("/") },
                                    Modifier.setVariable(ButtonVars.FontSize, 1.em),
                                    variant = UncoloredButtonVariant
                                ) {
                                    Text("Отмена")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
