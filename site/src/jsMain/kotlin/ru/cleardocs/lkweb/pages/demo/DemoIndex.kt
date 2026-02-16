package ru.cleardocs.lkweb.pages.demo

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.pages.demo.FakeData
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

        DemoNavCard(
            entries = listOf(
                Triple("/demo/profile", "ProfileBlock", "демо ProfileBlock с фейковым профилем"),
                Triple("/demo/profile-menu", "ProfileMenu", "демо ProfileMenu (меню профиля)"),
                Triple("/demo/layout", "Layout", "демо PageLayout и карточек"),
                Triple("/demo/plans", "PlansList", "демо списка тарифов"),
            )
        )
    }
}
