package ru.cleardocs.lkweb.connectors

sealed interface ConnectorsViewState {
    data object Loading : ConnectorsViewState
    data class Error(val message: String) : ConnectorsViewState
    data object GotoAuth : ConnectorsViewState
    sealed interface ConnectorsData : ConnectorsViewState {
        data class Connectors(val connectors: List<Connector>, val canAdd: Boolean = true) : ConnectorsData
        data object AddFile : ConnectorsData
        data object Chat : ConnectorsData
    }
}
