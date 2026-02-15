package ru.cleardocs.lkweb.pages

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel {

    private val _state = MutableStateFlow<MainViewState>(MainViewState.Profile)
    val stateFlow: StateFlow<MainViewState> = _state.asStateFlow()

    val menuEntries: List<Pair<MainViewState, String>> = listOf(
        MainViewState.Profile to "Профиль"
    )

    fun selectState(state: MainViewState) {
        _state.value = state
    }
}
