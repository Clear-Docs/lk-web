package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.compose.ui.graphics.Colors
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import ru.cleardocs.lkweb.pages.MainViewState
import ru.cleardocs.lkweb.toSitePalette
import ru.cleardocs.lkweb.UncoloredButtonVariant

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

@Suppress("PRIVATE_COMPONENT_STYLE")
private val ConfirmDialogBackdropStyle by ComponentStyle.base {
    Modifier
        .fillMaxSize()
        .position(Position.Fixed)
        .left(0.px)
        .top(0.px)
        .zIndex(200)
}

@Suppress("PRIVATE_COMPONENT_STYLE")
private val ConfirmDialogPanelStyle by ComponentStyle {
    base {
        val palette = colorMode.toSitePalette()
        Modifier
            .padding(1.5.cssRem)
            .borderRadius(1.cssRem)
            .backgroundColor(palette.nearBackground)
            .border(1.px, LineStyle.Solid, palette.cobweb)
            .boxShadow(4.px, 4.px, 12.px, color = Colors.Black.toRgb().copyf(alpha = 0.2f))
    }
}

@InitSilk
fun registerConfirmDialogStyles(ctx: InitSilkContext) {
    ctx.theme.registerComponentStyle(ConfirmDialogBackdropStyle)
    ctx.theme.registerComponentStyle(ConfirmDialogPanelStyle)
}

/**
 * Карточка меню профиля: заголовок «Меню», список пунктов с подсветкой текущего, пункт «Выйти».
 * При нажатии «Выйти» показывается диалог подтверждения.
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
    var showConfirmDialog by remember { mutableStateOf(false) }
    val palette = ColorMode.current.toSitePalette()

    val styleModifier = ProfileMenuCardColumnStyle.toModifier()

    Box(modifier) {
        Column(styleModifier.fillMaxWidth()) {
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
                    .onClick { it.preventDefault(); showConfirmDialog = true },
                variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
            ) {
                SpanText("Выйти")
            }
        }

        if (showConfirmDialog) {
            Box(
                ConfirmDialogBackdropStyle.toModifier()
                    .backgroundColor(Colors.Black.toRgb().copyf(alpha = 0.4f))
                    .onClick { showConfirmDialog = false },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    ConfirmDialogPanelStyle.toModifier()
                        .onClick { it.stopPropagation() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(Modifier.gap(1.cssRem)) {
                        SpanText("Вы действительно хотите выйти?", Modifier.fontSize(1.1.cssRem))
                        Row(Modifier.gap(0.75.cssRem)) {
                            Button(
                                onClick = {
                                    showConfirmDialog = false
                                    onSignOut()
                                },
                                Modifier.setVariable(ButtonVars.FontSize, 1.em)
                            ) {
                                SpanText("Да")
                            }
                            Button(
                                onClick = { showConfirmDialog = false },
                                Modifier.setVariable(ButtonVars.FontSize, 1.em),
                                variant = UncoloredButtonVariant
                            ) {
                                SpanText("Отмена")
                            }
                        }
                    }
                }
            }
        }
    }
}
