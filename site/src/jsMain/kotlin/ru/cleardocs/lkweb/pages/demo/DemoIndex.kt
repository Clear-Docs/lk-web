package ru.cleardocs.lkweb.pages.demo

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.plans.PlansList
import ru.cleardocs.lkweb.toSitePalette

/**
 * Индекс демо: ссылки на подразделы и превью PlansList.
 * Вызывается из [DemoRouterPage] при переходе на /demo.
 */
@Composable
fun DemoIndexPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.gap(1.5.cssRem)
    ) {
        SpanText("ClearDocs Demo")
        SpanText("Верстка с фейковыми данными", Modifier.padding(bottom = 0.5.cssRem))

        Column(horizontalAlignment = Alignment.Start) {
            Link("/demo/profile", "ProfileBlock", variant = UndecoratedLinkVariant.then(UncoloredLinkVariant))
            SpanText(" — демо ProfileBlock с фейковым профилем")
            Link("/demo/layout", "Layout", variant = UndecoratedLinkVariant.then(UncoloredLinkVariant))
            SpanText(" — демо PageLayout и карточек")
            Link("/demo/plans", "PlansList", variant = UndecoratedLinkVariant.then(UncoloredLinkVariant))
            SpanText(" — демо списка тарифов")
        }
    }
}
