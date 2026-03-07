package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
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
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Text
import ru.cleardocs.lkweb.components.layouts.PageLayout

@Page("/plans/connect")
@Composable
fun PlanConnectPage() {
    val ctx = rememberPageContext()

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
                SpanText("Функционал в разработке", Modifier.fontSize(1.25.cssRem))
                Button(onClick = { ctx.router.tryRoutingTo("/") }) {
                    Text("Вернуться в личный кабинет")
                }
            }
        }
    }
}
