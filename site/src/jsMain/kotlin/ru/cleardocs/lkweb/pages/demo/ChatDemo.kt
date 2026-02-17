package ru.cleardocs.lkweb.pages.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.components.sections.DisabledChatBlock
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.toSitePalette

@Composable
fun ChatDemoContent() {
    Box(
        Modifier
            .fillMaxWidth()
            .maxWidth(32.cssRem)
            .gap(1.25.cssRem)
            .cardSurface(ColorMode.current.toSitePalette()),
    ) {
        DisabledChatBlock(Modifier.fillMaxSize())
    }
}
