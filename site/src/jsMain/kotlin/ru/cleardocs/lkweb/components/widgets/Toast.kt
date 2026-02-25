package ru.cleardocs.lkweb.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.bottom
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent

@Composable
fun Toast(
    message: String,
    modifier: Modifier = Modifier,
) {
    Div(
        modifier
            .position(Position.Fixed)
            .bottom(2.cssRem)
            .left(50.percent)
            .padding(topBottom = 0.6.cssRem, leftRight = 1.2.cssRem)
            .backgroundColor(Colors.Black.toRgb().copyf(alpha = 0.8f))
            .color(Colors.White)
            .borderRadius(0.5.cssRem)
            .fontSize(0.9.cssRem)
            .zIndex(1000)
            .toAttrs {
                style {
                    property("transform", "translateX(-50%)")
                    property("pointer-events", "none")
                }
            }
    ) {
        SpanText(message)
    }
}
