package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayUntil
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.components.widgets.ContentCard
import ru.cleardocs.lkweb.components.sections.ProfileBlock
import ru.cleardocs.lkweb.SiteTokens
import ru.cleardocs.lkweb.toSitePalette

@Composable
internal fun ProfileContent() {
    val palette = ColorMode.current.toSitePalette()

    val profileBody: @Composable () -> Unit = {
        SpanText("Профиль", Modifier.fontSize(1.5.cssRem))
        ProfileBlock()
    }

    Box(Modifier.flexGrow(1).fillMaxSize()) {
        Column(
            Modifier
                .displayUntil(Breakpoint.MD)
                .flexGrow(1)
                .fillMaxSize()
                .gap(SiteTokens.Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            profileBody()
        }
        ContentCard(
            palette = palette,
            modifier = Modifier.displayIfAtLeast(Breakpoint.MD)
        ) {
            profileBody()
        }
    }
}
