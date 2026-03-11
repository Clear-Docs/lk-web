package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Text
import ru.cleardocs.lkweb.api.BackendApi
import ru.cleardocs.lkweb.components.layouts.PageLayout
import ru.cleardocs.lkweb.utils.toUserFriendlyMessage

private fun getUrlParam(name: String): String? {
    val params = js("new URLSearchParams(window.location.search)")
    return params.get(name) as? String
}

@Page("/plans/pay")
@Composable
fun PlanConnectPage() {
    val ctx = rememberPageContext()
    val planCode = remember { getUrlParam("plan") }
    var loading by remember { mutableStateOf(planCode != null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(planCode) {
        if (planCode == null) {
            loading = false
            return@LaunchedEffect
        }
        try {
            val response = BackendApi.createTochkaPayment(planCode)
            window.location.href = response.paymentUrl
        } catch (e: Throwable) {
            error = e.toUserFriendlyMessage("Ошибка создания платежа")
            loading = false
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
        }
    }
}
