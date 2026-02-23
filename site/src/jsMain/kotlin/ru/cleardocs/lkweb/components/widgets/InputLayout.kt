package ru.cleardocs.lkweb.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem

@Composable
fun InputLayout(
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier.fillMaxWidth().gap(0.25.cssRem).then(modifier)) {
        SpanText(label)
        content()
        error?.let { SpanText(it, Modifier.margin(top = 0.25.cssRem).color(com.varabyte.kobweb.compose.ui.graphics.Color.rgb(0xDC2626))) }
    }
}
