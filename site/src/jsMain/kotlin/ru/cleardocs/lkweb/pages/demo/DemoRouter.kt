package ru.cleardocs.lkweb.pages.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.components.layouts.PageLayout
import ru.cleardocs.lkweb.toSitePalette

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
        "profile-menu" -> "Demo — ProfileMenu"
        "layout" -> "Demo — Layout"
        "chat" -> "Demo — ChatBlock"
        else -> "Demo"
    }
    PageLayout(title = pageTitle) {
        Box(
            Modifier.fillMaxSize().padding(3.cssRem),
            contentAlignment = Alignment.Center
        ) {
            when (subRoute) {
                "" -> DemoIndexPage()
                "plans", "profile", "profile-menu", "layout", "chat" -> Column(Modifier.gap(1.cssRem)) {
                    Link(
                        "/demo",
                        "← Назад к демо",
                        variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
                    )
                    when (subRoute) {
                        "plans" -> PlansDemoContent()
                        "profile" -> ProfileDemoContent()
                        "profile-menu" -> ProfileMenuDemoContent()
                        "layout" -> LayoutDemoContent()
                        "chat" -> ChatDemoContent()
                        else -> {}
                    }
                }
                else -> SpanText("Демо не найдено: /demo/$subRoute")
            }
        }
    }
}
