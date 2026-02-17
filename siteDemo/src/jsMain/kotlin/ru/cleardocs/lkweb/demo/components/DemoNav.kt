package ru.cleardocs.lkweb.demo.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.toSitePalette

@Composable
fun DemoNavLink(path: String, text: String) {
    Link(
        path,
        text,
        modifier = Modifier.padding(0.5.cssRem),
        variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
    )
}

/**
 * Карточка навигации по демо-страницам в стиле ProfileMenuCard.
 */
@Composable
fun DemoNavCard(
    entries: List<Triple<String, String, String>>,
    modifier: Modifier = Modifier,
) {
    val palette = ColorMode.current.toSitePalette()
    val columnModifier = modifier
        .gap(0.75.cssRem)
        .cardSurface(palette, padding = 1.cssRem, borderRadius = 1.cssRem)

    Column(columnModifier) {
        SpanText("Демо", Modifier.fontSize(1.05.cssRem))
        for ((path, label, description) in entries) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.65.cssRem)
                    .borderRadius(0.75.cssRem),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Link(
                    path,
                    label,
                    variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
                )
                SpanText(" — $description")
            }
        }
    }
}

@Composable
fun DemoMenuItems() {
    DemoNavLink("/demo", "Demo")
    DemoNavLink("/demo/profile", "ProfileBlock")
    DemoNavLink("/demo/profile-menu", "ProfileMenu")
    DemoNavLink("/demo/layout", "Layout")
    DemoNavLink("/demo/plans", "PlansList")
    DemoNavLink("/demo/chat", "DisabledChatBlock")
}
