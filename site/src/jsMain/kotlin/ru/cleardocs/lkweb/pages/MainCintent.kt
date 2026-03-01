package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable
import ru.cleardocs.lkweb.profile.MeViewModel

@Composable
internal fun MainContent(mainState: MainViewState, meViewModel: MeViewModel) {
    when (mainState) {
        MainViewState.Profile -> ProfileContent(meViewModel = meViewModel)
        MainViewState.Connectors -> ConnectorsContent()
        MainViewState.Plans -> PlansContent(meViewModel = meViewModel)
    }
}
