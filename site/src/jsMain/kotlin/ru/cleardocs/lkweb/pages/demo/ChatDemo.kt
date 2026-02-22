package ru.cleardocs.lkweb.pages.demo

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.components.sections.ChatBlock
import ru.cleardocs.lkweb.components.widgets.cardSurface
import ru.cleardocs.lkweb.toSitePalette

/** TODO: убрать перед коммитом */
private const val ONYX_API_KEY = "on_Q0bMtaezejuPRxuYcAUum74EXPPT5dEdF5ZMWHcHEPhJDT5HSQNuuIIamaPNDblT6jjTkgnq_tqiCdKWnOQ4nVhtC-Qsl4-7FlPLi4AIL0_WGz1zr7iBWfho0Pk83gmC7wxaJDs4vYo3nC7xZ28v5tssUR1eXI3bc3tNF95Avnv4rduB9Zma5kY24wmcoFlovVfTXHdIQJ6EOgrToI3MQYTbjfpeWBZ1tWoZsKEREFbNZ1vSl9h0FVLg8DV8HvZ8"

@Composable
fun ChatDemoContent() {
    Box(
        Modifier
            .fillMaxWidth()
            .maxWidth(62.cssRem)
            .gap(1.25.cssRem)
            .cardSurface(ColorMode.current.toSitePalette()),
    ) {
        ChatBlock(
            modifier = Modifier.fillMaxSize(),
            personaId = 1,
            apiKey = ONYX_API_KEY,
        )
    }
}
