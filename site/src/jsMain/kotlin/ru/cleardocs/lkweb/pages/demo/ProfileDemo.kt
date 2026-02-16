package ru.cleardocs.lkweb.pages.demo

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.components.sections.ProfileBlock
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.toSitePalette

@Composable
fun ProfileDemoContent() {
    Column(
        Modifier
            .fillMaxWidth()
            .maxWidth(32.cssRem)
            .gap(1.25.cssRem)
            .cardSurface(ColorMode.current.toSitePalette()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpanText("ProfileBlock (фейковые данные)", Modifier.padding(bottom = 0.5.cssRem))
        SpanText("С аватаром:", Modifier.fillMaxWidth())
        ProfileBlock(profile = FakeData.profileWithPhoto)
        SpanText("Без аватара:", Modifier.fillMaxWidth().padding(top = 1.5.cssRem))
        ProfileBlock(profile = FakeData.profileWithoutPhoto)
        SpanText("Минимальные данные:", Modifier.fillMaxWidth().padding(top = 1.5.cssRem))
        ProfileBlock(profile = FakeData.profileMinimal)
    }
}
