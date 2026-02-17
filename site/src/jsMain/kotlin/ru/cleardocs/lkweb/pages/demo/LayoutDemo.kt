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
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.toSitePalette

@Composable
fun LayoutDemoContent() {
    Column(
        Modifier
            .fillMaxWidth()
            .maxWidth(40.cssRem)
            .gap(1.5.cssRem)
            .padding(2.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpanText("Layout Demo — карточки с фейковым контентом", Modifier.padding(bottom = 0.5.cssRem))
        repeat(3) { i ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .cardSurface(
                        ColorMode.current.toSitePalette(),
                        padding = 1.5.cssRem,
                        borderRadius = 1.cssRem
                    ),
                horizontalAlignment = Alignment.Start
            ) {
                SpanText("Карточка ${i + 1}", Modifier.padding(bottom = 0.5.cssRem))
                SpanText("Пример фейкового контента для проверки верстки.")
            }
        }
    }
}
