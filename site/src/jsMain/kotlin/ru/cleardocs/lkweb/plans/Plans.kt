package ru.cleardocs.lkweb.plans

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.toSitePalette

/**
 * Секция «Тарифы»: при первом появлении создаётся ViewModel и вызывается REST GET /api/v1/plans.
 * Показывает загрузку, ошибку или список тарифов.
 */
@Composable
fun Plans(currentPlanCode: String? = null) {
    val viewModel = remember { PlansViewModel(currentPlanCode = currentPlanCode) }
    val plans by viewModel.plans.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(Modifier.fillMaxWidth().gap(1.25.cssRem)) {
        SpanText("Тарифы", Modifier.fontSize(1.5.cssRem))
        when {
            loading -> SpanText("Загрузка тарифов...")
            error != null -> SpanText("Ошибка: $error")
            plans.isEmpty() -> SpanText("Нет доступных тарифов.")
            else -> PlansList(plans = plans)
        }
    }
}

/**
 * Список карточек тарифов — переиспользуемый компонент для отображения [plans].
 */
@Composable
fun PlansList(plans: List<Plan>) {
    val palette = ColorMode.current.toSitePalette()
    Column(Modifier.fillMaxWidth().gap(1.cssRem)) {
        plans.forEach { plan -> PlanCard(plan = plan, palette = palette) }
    }
}

@Composable
private fun PlanCard(plan: Plan, palette: ru.cleardocs.lkweb.SitePalette) {
    Column(
        Modifier
            .fillMaxWidth()
            .cardSurface(
                palette,
                padding = 1.25.cssRem,
                borderRadius = 1.cssRem
            )
            .then(
                if (plan.isActive) Modifier.border(2.px, LineStyle.Solid, palette.brand.primary)
                else Modifier
            )
            .gap(0.5.cssRem)
    ) {
        Row(Modifier.fillMaxWidth().gap(0.5.cssRem)) {
            SpanText(plan.title, Modifier.fontSize(1.15.cssRem))
            if (plan.isActive) {
                SpanText(
                    "Выбран",
                    Modifier
                        .padding(topBottom = 0.2.cssRem, leftRight = 0.5.cssRem)
                        .fontSize(0.8.cssRem)
                        .backgroundColor(palette.brand.primary)
                        .color(Colors.White)
                )
            }
        }
        SpanText("Цена: ${plan.priceRub} ₽")
        if (plan.periodDays > 0) {
            SpanText("Период: ${plan.periodDays} дн.")
        }
        SpanText("Лимит коннекторов: ${plan.limit.maxConnectors}")
        if (!plan.isActive) {
            val ctx = rememberPageContext()
            Button(
                onClick = { ctx.router.navigateTo("/profile") },
                modifier = Modifier.padding(0.25.cssRem)
            ) {
                Text("Выбрать")
            }
        }
    }
}
