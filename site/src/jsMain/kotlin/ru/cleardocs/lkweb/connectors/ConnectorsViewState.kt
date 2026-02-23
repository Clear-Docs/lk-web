package ru.cleardocs.lkweb.connectors

sealed interface ConnectorsViewState {
    data object Loading : ConnectorsViewState
    data class Error(val message: String) : ConnectorsViewState
    data object GotoAuth : ConnectorsViewState
    data class ConnectorsData(val connectors: List<Connector>) : ConnectorsViewState
}
