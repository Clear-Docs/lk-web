package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import ru.cleardocs.lkweb.plans.Plans
import ru.cleardocs.lkweb.SiteTokens

@Composable
internal fun PlansContent() {
    Column(
        Modifier
            .flexGrow(1)
            .fillMaxSize()
            .gap(SiteTokens.Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Plans()
    }
}
