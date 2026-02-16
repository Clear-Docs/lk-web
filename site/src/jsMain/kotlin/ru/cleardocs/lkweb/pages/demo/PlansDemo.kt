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
import ru.cleardocs.lkweb.api.dto.LimitDto
import ru.cleardocs.lkweb.api.dto.PlanDto
import ru.cleardocs.lkweb.plans.PlansList
import ru.cleardocs.lkweb.toSitePalette

@Composable
fun PlansDemoContent() {
    val palette = ColorMode.current.toSitePalette()
    Column(
        Modifier
            .fillMaxWidth()
            .maxWidth(36.cssRem)
            .gap(1.5.cssRem),
        horizontalAlignment = Alignment.Start
    ) {
        SpanText("PlansList (фейковые тарифы)", Modifier.padding(bottom = 0.5.cssRem))

        SpanText("Полный список:", Modifier.fillMaxWidth())
        PlansList(plans = FakeData.plans, palette = palette)

        SpanText("Один тариф:", Modifier.fillMaxWidth().padding(top = 0.5.cssRem))
        PlansList(
            plans = listOf(
                PlanDto(
                    code = "trial",
                    title = "Пробный",
                    priceRub = 0,
                    periodDays = 14,
                    limit = LimitDto(maxConnectors = 2),
                )
            ),
            palette = palette,
        )

        SpanText("Пустой список:", Modifier.fillMaxWidth().padding(top = 0.5.cssRem))
        PlansList(plans = emptyList(), palette = palette)
    }
}
