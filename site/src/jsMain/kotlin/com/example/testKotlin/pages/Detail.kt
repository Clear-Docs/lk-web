package com.example.testKotlin.pages
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.testKotlin.HeadlineTextStyle
import com.example.testKotlin.SubheadlineTextStyle
import com.example.testKotlin.components.layouts.PageLayout
import com.example.testKotlin.toSitePalette
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.textShadow
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.toAttrs
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorSchemes
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
@Page
@Composable
fun DetailPage() {
    PageLayout("Detail") {
        val sitePalette = ColorMode.current.toSitePalette()
        Column(Modifier.gap(2.cssRem)) {
            Div(HeadlineTextStyle.toAttrs()) {
                SpanText(
                    "Demo TextInput in ", Modifier.color(
                        when (ColorMode.current) {
                            ColorMode.LIGHT -> Colors.Black
                            ColorMode.DARK -> Colors.White
                        }
                    )
                )
                SpanText(
                    "Kotlin",
                    Modifier
                        .color(sitePalette.brand.accent)
                        // Use a shadow so this light-colored word is more visible in light mode
                        .textShadow(0.px, 0.px, blurRadius = 0.5.cssRem, color = Colors.Gray)
                )
            }
        }
        P()
        Text("Please enter your name")
        var name by remember { mutableStateOf("") }
        var input = Input(
            InputType.Text,
            attrs = {
                onInput { e -> name = e.value }
            }
        )
        P()
        Text("Hello ${name.takeIf { it.isNotBlank() } ?: "World"}!")


    }
}