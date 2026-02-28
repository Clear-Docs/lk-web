package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import ru.cleardocs.lkweb.plans.Plans
import ru.cleardocs.lkweb.profile.MeViewModel
import ru.cleardocs.lkweb.SiteTokens

@Composable
internal fun PlansContent(meViewModel: MeViewModel) {
    val me by meViewModel.me.collectAsState()
    val currentPlanCode = me?.plan?.code

    Column(
        Modifier
            .flexGrow(1)
            .fillMaxSize()
            .gap(SiteTokens.Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Plans(currentPlanCode = currentPlanCode)
    }
}
