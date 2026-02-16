package ru.cleardocs.lkweb.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnit
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import ru.cleardocs.lkweb.SitePalette

/**
 * Modifier extension for card-like surfaces (background, border, radius, padding).
 */
fun Modifier.cardSurface(
    palette: SitePalette,
    padding: CSSSizeValue<CSSUnit.rem> = 2.cssRem,
    borderRadius: CSSSizeValue<CSSUnit.rem> = 1.25.cssRem,
) = this
    .padding(padding)
    .borderRadius(borderRadius)
    .backgroundColor(palette.nearBackground)
    .border(1.px, LineStyle.Solid, palette.cobweb)

/**
 * Column with card surface styling. Use for content cards.
 */
@Composable
fun CardSurface(
    modifier: Modifier = Modifier,
    palette: SitePalette,
    padding: CSSSizeValue<CSSUnit.rem> = 2.cssRem,
    borderRadius: CSSSizeValue<CSSUnit.rem> = 1.25.cssRem,
    gap: CSSSizeValue<CSSUnit.rem> = 1.25.cssRem,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .cardSurface(palette, padding, borderRadius)
            .gap(gap),
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}
