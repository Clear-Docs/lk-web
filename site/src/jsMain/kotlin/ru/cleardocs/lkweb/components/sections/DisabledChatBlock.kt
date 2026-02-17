package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Text
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.toSitePalette

/**
 * Блок «чат недоступен»: показывает заблюренный placeholder чата,
 * текст «Для активации чата добавьте контроллер» и кнопку «Добавить».
 * Не принимает onButtonClick — чисто презентационный компонент.
 */
@Composable
fun DisabledChatBlock(
    modifier: Modifier = Modifier,
) {
    val palette = ColorMode.current.toSitePalette()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.cssRem)
    ) {
        // Placeholder чата (приглушённый)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .gap(0.75.cssRem)
                .padding(0.5.cssRem)
                .backgroundColor(palette.nearBackground),
        ) {
            repeat(4) { i ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (i % 2 == 0) Arrangement.Start else Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (i % 2 == 0) 60.percent else 50.percent)
                            .backgroundColor(palette.cobweb),
                    ) {
                        SpanText("Сообщение ${i + 1}", Modifier.padding(0.5.cssRem))
                    }
                }
            }
        }

        // Оверлей с полупрозрачным фоном, текстом и кнопкой
        Box(
            modifier = Modifier
                .fillMaxSize()
                .backgroundColor(palette.nearBackground.toRgb().copyf(alpha = 0.92f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.gap(1.cssRem)
            ) {
                SpanText(
                    "Для активации чата добавьте контроллер",
                    Modifier
                        .padding(1.cssRem)
                        .fillMaxWidth()
                        .textAlign(TextAlign.Center)
                )
                Button(
                    onClick = { },
                    modifier = Modifier.padding(0.25.cssRem)
                ) {
                    Text("Добавить")
                }
            }
        }
    }
}
