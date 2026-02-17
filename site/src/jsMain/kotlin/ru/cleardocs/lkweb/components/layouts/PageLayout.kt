package ru.cleardocs.lkweb.components.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.browser.document
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.vh
import ru.cleardocs.lkweb.components.sections.Footer
import ru.cleardocs.lkweb.components.sections.NavHeader
import ru.cleardocs.lkweb.toSitePalette

val PageContentStyle by ComponentStyle {
    base { Modifier.fillMaxSize().padding(leftRight = 1.cssRem, top = 4.cssRem) }
    Breakpoint.MD { Modifier.maxWidth(60.cssRem) }
}

@Composable
fun PageLayout(
    title: String,
    menuItems: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    document.title = "ClearDocs - $title"

    Column(
        Modifier
            .fillMaxWidth()
            .minHeight(100.vh)
            .flexDirection(FlexDirection.Column),
        horizontalAlignment = Alignment.Start,
    ) {
        Column(
            Modifier.flexGrow(1).fillMaxWidth(),
            horizontalAlignment = Alignment.Start, // Start allows children with fillMaxWidth to stretch; Center shrinks them
        ) {
            NavHeader()
            content()
        }
        Footer(Modifier.fillMaxWidth())
    }
}
