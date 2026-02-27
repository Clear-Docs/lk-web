package ru.cleardocs.lkweb.pages

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel {

    private val _state = MutableStateFlow<MainViewState>(MainViewState.Connectors)
    val stateFlow: StateFlow<MainViewState> = _state.asStateFlow()

    val menuEntries: List<Pair<MainViewState, String>> = listOf(
        MainViewState.Connectors to "Коннекторы",
        MainViewState.Profile to "Профиль",
        MainViewState.Plans to "Тарифы"
    )

    fun selectState(state: MainViewState) {
        _state.value = state
    }
}
