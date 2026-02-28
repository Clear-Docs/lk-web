package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.pages.MainViewState
import ru.cleardocs.lkweb.toSitePalette

/**
 * Карточка меню профиля: заголовок «Меню», список пунктов с подсветкой текущего, пункт «Выйти».
 * Чистый UI-компонент без DI и ViewModel.
 */
@Composable
fun ProfileMenuCard(
    menuEntries: List<Pair<MainViewState, String>>,
    selected: MainViewState,
    onEntrySelected: (MainViewState) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = ColorMode.current.toSitePalette()

    val columnModifier = modifier
        .gap(0.75.cssRem)
        .cardSurface(palette, padding = 1.cssRem, borderRadius = 1.cssRem)

    Column(columnModifier) {
        SpanText("Меню", Modifier.fontSize(1.05.cssRem))
        for ((state, label) in menuEntries) {
            val highlighted = state == selected
            Link(
                "#",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.65.cssRem)
                    .borderRadius(0.75.cssRem)
                    .then(if (highlighted) Modifier.backgroundColor(palette.cobweb) else Modifier)
                    .onClick { it.preventDefault(); onEntrySelected(state) },
                variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
            ) {
                SpanText(label)
            }
        }
        Link(
            "#",
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.65.cssRem)
                .borderRadius(0.75.cssRem)
                .onClick { it.preventDefault(); onSignOut() },
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
        ) {
            SpanText("Выйти")
        }
    }
}
