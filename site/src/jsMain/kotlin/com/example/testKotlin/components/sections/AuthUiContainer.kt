package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import ru.cleardocs.lkweb.toSitePalette
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Div

@Composable
fun AuthUiContainer() {
    val palette = ColorMode.current.toSitePalette()
    Column(Modifier.fillMaxWidth().gap(1.cssRem)) {
        SpanText("Вход или регистрация", Modifier.fontSize(1.1.cssRem))
        Div(
            Modifier
                .fillMaxWidth()
                .minHeight(18.cssRem)
                .padding(0.5.cssRem)
                .borderRadius(0.75.cssRem)
                .backgroundColor(palette.nearBackground)
                .toAttrs { id("firebaseui-auth-container") }
        )
    }
}
