package ru.cleardocs.lkweb.pages.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.components.layouts.PageLayout

@Page("/demo/{...path?}")
@Composable
fun DemoRouterPage() {
    val ctx = rememberPageContext()
    val subRoute = remember(ctx.route.path) {
        val path = ctx.route.path
        val afterDemo = path.removePrefix("/demo").trimStart('/')
        afterDemo.split("/").firstOrNull()?.takeIf { it.isNotBlank() } ?: ""
    }

    val pageTitle = when (subRoute) {
        "" -> "Demo"
        "plans" -> "Demo — PlansList"
        "profile" -> "Demo — ProfileBlock"
        "layout" -> "Demo — Layout"
        else -> "Demo"
    }
    PageLayout(title = pageTitle) {
        Box(
            Modifier.fillMaxSize().padding(3.cssRem),
            contentAlignment = Alignment.Center
        ) {
            when (subRoute) {
                "" -> DemoIndexPage()
                "plans", "profile", "layout" -> Column(Modifier.gap(1.cssRem)) {
                    Link(
                        "/demo",
                        "← Назад к демо",
                        variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
                    )
                    when (subRoute) {
                        "plans" -> PlansDemoContent()
                        "profile" -> ProfileDemoContent()
                        "layout" -> LayoutDemoContent()
                        else -> {}
                    }
                }
                else -> SpanText("Демо не найдено: /demo/$subRoute")
            }
        }
    }
}
