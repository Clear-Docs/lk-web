package ru.cleardocs.lkweb.connectors

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.boxShadow
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import ru.cleardocs.lkweb.cardItemBackground
import ru.cleardocs.lkweb.SitePalette
import ru.cleardocs.lkweb.SiteTokens
import ru.cleardocs.lkweb.toSitePalette

@Composable
fun ConnectorItem(
    connector: Connector,
    palette: SitePalette,
    onDelete: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
) {
    val textColor = ColorMode.current.toPalette().color
    val statusUpper = connector.status?.uppercase()
    val iconSrc = when (connector.type.uppercase()) {
        "URL" -> "/globe-icon.svg"
        "1C" -> "/1c.png"
        "NOTION" -> "/notion-icon.png"
        else -> "/file-icon.svg"
    }
    val itemBg = ColorMode.current.cardItemBackground(palette)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(SiteTokens.Spacing.xl)
            .borderRadius(SiteTokens.Radius.md)
            .backgroundColor(itemBg)
            .boxShadow(2.px, 2.px, 8.px, color = palette.brand.primary.toRgb().copyf(alpha = 0.12f))
            .gap(SiteTokens.Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Img(
            src = iconSrc,
            alt = connector.name,
            attrs = {
                style {
                    property("width", "2rem")
                    property("height", "2rem")
                    property("object-fit", "contain")
                    property("flex-shrink", "0")
                }
            }
        )
        Div(
            attrs = {
                style {
                    property("flex-grow", "1")
                    property("overflow", "hidden")
                    property("text-overflow", "ellipsis")
                    property("min-width", "0")
                }
            }
        ) {
            SpanText(connector.name, Modifier.color(textColor.toRgb()).fontSize(1.cssRem))
        }
        ConnectorStatusBadge(connector.status, Modifier.flexShrink(0))
        ConnectorStatusButtons(
            connectorId = connector.id,
            statusUpper = statusUpper,
            onPause = onPause,
            onResume = onResume,
            onDelete = onDelete,
        )
    }
}

@Composable
fun ConnectorsList(
    connectors: List<Connector>,
    onDelete: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
) {
    val palette = ColorMode.current.toSitePalette()
    Column(
        Modifier
            .fillMaxWidth()
            .gap(SiteTokens.Spacing.xl)
    ) {
        connectors.forEach { c ->
            ConnectorItem(
                connector = c,
                palette = palette,
                onDelete = onDelete,
                onPause = onPause,
                onResume = onResume,
            )
        }
    }
}

@Composable
fun NoConnectorsMessage() {
    SpanText("Нет коннекторов.")
}
