package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import ru.cleardocs.lkweb.pages.MainViewState
import ru.cleardocs.lkweb.toSitePalette

@Suppress("PRIVATE_COMPONENT_STYLE")
private val ProfileMenuCardColumnStyle by ComponentStyle {
    base {
        Modifier.gap(0.75.cssRem)
    }
    Breakpoint.MD {
        val palette = colorMode.toSitePalette()
        Modifier
            .padding(1.cssRem)
            .borderRadius(1.cssRem)
            .backgroundColor(palette.nearBackground)
            .border(1.px, LineStyle.Solid, palette.cobweb)
    }
}

@InitSilk
fun registerProfileMenuCardStyle(ctx: InitSilkContext) {
    ctx.theme.registerComponentStyle(ProfileMenuCardColumnStyle)
}

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

    val columnModifier = modifier.then(ProfileMenuCardColumnStyle.toModifier())

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
