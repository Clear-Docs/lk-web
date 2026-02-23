package ru.cleardocs.lkweb.chat

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Span
import ru.cleardocs.lkweb.SitePalette

private const val MAX_CITATION_TEXT_LENGTH = 40

private fun truncateText(str: String, maxLength: Int = MAX_CITATION_TEXT_LENGTH): String {
    if (str.length <= maxLength) return str
    return str.take(maxLength) + "..."
}

/**
 * Инлайн-бейдж цитаты (источника) в тексте сообщения чата.
 * По аналогии с Onyx SourceTag (variant inlineCitation).
 */
@Composable
fun CitationBadge(
    displayName: String,
    palette: SitePalette,
    modifier: Modifier = Modifier,
) {
    val label = displayName.ifBlank { "File" }.let { truncateText(it) }
    val fullTitle = displayName.ifBlank { "File" }
    val textColor = when (ColorMode.current) {
        ColorMode.LIGHT -> Color.rgb(0x334155)
        ColorMode.DARK -> Color.rgb(0xE2E8F0)
    }

    Span(
        modifier.toAttrs {
            style {
                property("display", "inline-flex")
                property("align-items", "center")
            }
        }
    ) {
        Span(
            Modifier
                .margin(left = 0.25.cssRem)
                .borderRadius(0.4.cssRem)
                .padding(0.2.cssRem, 0.5.cssRem)
                .fontSize(0.8.cssRem)
                .color(textColor)
                .toAttrs {
                    style {
                        property("background", palette.cobweb.toString())
                    }
                }
        ) {
            Row(
                modifier = Modifier.gap(0.25.cssRem),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Img(
                    src = "/citation.svg",
                    alt = "",
                    attrs = {
                        style {
                            property("width", "0.9rem")
                            property("height", "0.9rem")
                            property("flex-shrink", "0")
                        }
                    }
                )
                SpanText(label)
            }
        }
        Tooltip(
            ElementTarget.PreviousSibling,
            fullTitle,
            placement = PopupPlacement.Top
        )
    }
}
