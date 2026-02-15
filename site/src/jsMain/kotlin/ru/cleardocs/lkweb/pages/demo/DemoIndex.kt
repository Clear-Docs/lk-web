package ru.cleardocs.lkweb.pages.demo

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.components.layouts.PageLayout

@Page("/demo")
@Composable
fun DemoIndexPage() {
    PageLayout(
        title = "Demo"
    ) {
        Box(
            Modifier.fillMaxSize().padding(3.cssRem),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SpanText("ClearDocs Demo", Modifier.padding(bottom = 1.cssRem))
                SpanText("Верстка с фейковыми данными", Modifier.padding(bottom = 1.5.cssRem))
                Column(horizontalAlignment = Alignment.Start) {
                    Link("/demo/profile", "ProfileBlock", variant = UndecoratedLinkVariant.then(UncoloredLinkVariant))
                    SpanText(" — демо ProfileBlock с фейковым профилем")
                    Link("/demo/layout", "Layout", variant = UndecoratedLinkVariant.then(UncoloredLinkVariant))
                    SpanText(" — демо PageLayout и карточек")
                }
            }
        }
    }
}
