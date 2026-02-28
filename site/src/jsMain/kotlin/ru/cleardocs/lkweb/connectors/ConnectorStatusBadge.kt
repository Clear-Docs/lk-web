package ru.cleardocs.lkweb.connectors

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Span

/**
 * Маппинг статусов коннектора по Onyx ConnectorCredentialPairStatus.
 * Enum: SCHEDULED, INITIAL_INDEXING, ACTIVE, PAUSED, DELETING, INVALID
 */
fun connectorStatusLabel(status: String?): String = when (status?.uppercase()) {
    "ACTIVE" -> "Готов"
    "SCHEDULED", "INITIAL_INDEXING" -> "Индексация"
    "INVALID" -> "Ошибка"
    "PAUSED" -> "Приостановлен"
    "DELETING" -> "Удаление"
    null, "" -> "—"
    else -> status
}

fun connectorStatusColors(status: String?): Pair<Color, Color> = when (status?.uppercase()) {
    "ACTIVE" -> Color.rgb(0x22C55E) to Colors.White
    "SCHEDULED", "INITIAL_INDEXING" -> Color.rgb(0xF59E0B) to Color.rgb(0x1F2937)
    "INVALID" -> Color.rgb(0xDC2626) to Colors.White
    "PAUSED" -> Color.rgb(0x94A3B8) to Color.rgb(0x1E293B)
    "DELETING" -> Color.rgb(0x64748B) to Colors.White
    else -> Color.rgb(0x94A3B8) to Color.rgb(0x1E293B)
}

@Composable
fun ConnectorStatusBadge(status: String?, modifier: Modifier = Modifier) {
    val label = connectorStatusLabel(status)
    val (bg, text) = connectorStatusColors(status)
    Span(
        modifier
            .borderRadius(0.35.cssRem)
            .padding(left = 0.45.cssRem, right = 0.45.cssRem, top = 0.2.cssRem, bottom = 0.2.cssRem)
            .fontSize(0.75.cssRem)
            .toAttrs {
                style {
                    property("background", bg.toString())
                    property("color", text.toString())
                    property("font-weight", "500")
                    property("white-space", "nowrap")
                }
            }
    ) {
        SpanText(label)
    }
}

@Composable
fun ConnectorStatusButtons(
    connectorId: String,
    statusUpper: String?,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    if (statusUpper == "ACTIVE") {
        Button(
            onClick = { onPause(connectorId) },
            modifier = Modifier.fontSize(0.8.cssRem).padding(0.2.cssRem)
        ) {
            SpanText("Пауза")
        }
    }
    if (statusUpper == "PAUSED") {
        Button(
            onClick = { onResume(connectorId) },
            modifier = Modifier.fontSize(0.8.cssRem).padding(0.2.cssRem)
        ) {
            SpanText("Возобновить")
        }
        Button(
            onClick = { onDelete(connectorId) },
            modifier = Modifier.fontSize(0.8.cssRem).padding(0.2.cssRem)
        ) {
            SpanText("Удалить")
        }
    }
}
