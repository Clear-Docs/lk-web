package ru.cleardocs.lkweb.plans

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.api.dto.PlanDto
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.toSitePalette

/**
 * Секция «Тарифы»: при первом появлении создаётся ViewModel и вызывается REST GET /api/v1/plans.
 * Показывает загрузку, ошибку или список тарифов.
 */
@Composable
fun Plans() {
    val viewModel = remember { PlansViewModel() }
    val plans by viewModel.plans.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val palette = ColorMode.current.toSitePalette()

    Column(Modifier.fillMaxWidth().gap(1.25.cssRem)) {
        SpanText("Тарифы", Modifier.fontSize(1.5.cssRem))
        when {
            loading -> SpanText("Загрузка тарифов...")
            error != null -> SpanText("Ошибка: $error")
            plans.isEmpty() -> SpanText("Нет доступных тарифов.")
            else -> PlansList(plans = plans, palette = palette)
        }
    }
}

/**
 * Список карточек тарифов — переиспользуемый компонент для отображения [plans].
 */
@Composable
fun PlansList(
    plans: List<PlanDto>,
    palette: ru.cleardocs.lkweb.SitePalette,
) {
    Column(Modifier.fillMaxWidth().gap(1.cssRem)) {
        plans.forEach { plan -> PlanCard(plan = plan, palette = palette) }
    }
}

@Composable
private fun PlanCard(plan: PlanDto, palette: ru.cleardocs.lkweb.SitePalette) {
    Column(
        Modifier
            .fillMaxWidth()
            .cardSurface(palette, padding = 1.25.cssRem, borderRadius = 1.cssRem)
            .gap(0.5.cssRem)
    ) {
        SpanText(plan.title, Modifier.fontSize(1.15.cssRem))
        SpanText("Цена: ${plan.priceRub} ₽")
        if (plan.periodDays > 0) {
            SpanText("Период: ${plan.periodDays} дн.")
        }
        SpanText("Лимит коннекторов: ${plan.limit.maxConnectors}")
    }
}
