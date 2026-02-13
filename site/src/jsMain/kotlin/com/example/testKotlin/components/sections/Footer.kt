package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.LineStyle
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Span
import ru.cleardocs.lkweb.toSitePalette

val FooterStyle by ComponentStyle.base {
    val palette = colorMode.toSitePalette()
    Modifier
        .borderTop(1.px, LineStyle.Solid, palette.cobweb)
        .backgroundColor(palette.nearBackground)
        .padding(topBottom = 1.5.cssRem, leftRight = 2.cssRem)
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Box(FooterStyle.toModifier().then(modifier), contentAlignment = Alignment.Center) {
        Span(Modifier.textAlign(TextAlign.Center).toAttrs()) {
            SpanText("ClearDocs © 2025")
        }
    }
}
