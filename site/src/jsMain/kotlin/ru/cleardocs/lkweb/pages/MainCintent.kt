package ru.cleardocs.lkweb.pages

import androidx.compose.runtime.Composable

@Composable
internal fun MainContent(mainState: MainViewState) {
    when (mainState) {
        MainViewState.Profile -> ProfileContent()
        MainViewState.Connectors -> ConnectorsContent()
        MainViewState.Plans -> PlansContent()
    }
}
