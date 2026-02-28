package ru.cleardocs.lkweb.connectors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.cleardocs.lkweb.api.BackendApi
import ru.cleardocs.lkweb.utils.isUnauthError
import ru.cleardocs.lkweb.utils.toUserFriendlyMessage

private const val POLL_INTERVAL_MS = 10_000L

private fun List<Connector>.allActive(): Boolean =
    isEmpty() || all { it.status?.uppercase() == "ACTIVE" }

class ConnectorsViewModel(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {

    private val _state = MutableStateFlow<ConnectorsViewState>(ConnectorsViewState.Loading)
    val state: StateFlow<ConnectorsViewState> = _state.asStateFlow()

    private var lastConnectors: List<Connector> = emptyList()
    private var lastCanAdd: Boolean = true

    private var pollJob: Job? = null

    init {
        scope.launch { loadConnectors() }
    }

    fun goToChat() {
        val current = _state.value
        if (current is ConnectorsViewState.ConnectorsData.Connectors && current.connectors.isNotEmpty()) {
            lastConnectors = current.connectors
            lastCanAdd = current.canAdd
            stopPolling()
            _state.value = ConnectorsViewState.ConnectorsData.Chat
        }
    }

    fun backFromChat() {
        _state.value = ConnectorsViewState.ConnectorsData.Connectors(lastConnectors, lastCanAdd)
        if (!lastConnectors.allActive()) startPolling()
    }

    fun stopPolling() {
        pollJob?.cancel()
        pollJob = null
    }

    /**
     * Загружает список коннекторов из REST GET /api/v1/connectors.
     * При успехе: если не все ACTIVE — запускает пуллинг раз в 10 сек; когда все ACTIVE — останавливает.
     */
    suspend fun loadConnectors() {
        pollJob?.cancel()
        _state.value = ConnectorsViewState.Loading
        try {
            val response = BackendApi.connectors()
            val connectors = response.connectors.map { dto ->
                Connector(
                    id = dto.id.toString(),
                    name = dto.name,
                    type = dto.type,
                    status = dto.status,
                )
            }
            onConnectorsLoaded(connectors, response.canAdd)
        } catch (e: Throwable) {
            val errorMsg = e.toUserFriendlyMessage("Ошибка загрузки коннекторов")
            _state.value = if (errorMsg.isUnauthError()) {
                ConnectorsViewState.GotoAuth
            } else {
                ConnectorsViewState.Error(errorMsg)
            }
        }
    }

    private fun onConnectorsLoaded(connectors: List<Connector>, canAdd: Boolean) {
        lastConnectors = connectors
        lastCanAdd = canAdd
        val current = _state.value
        if (current !is ConnectorsViewState.ConnectorsData.Chat) {
            _state.value = ConnectorsViewState.ConnectorsData.Connectors(connectors, canAdd)
        }
        if (connectors.allActive()) {
            pollJob?.cancel()
            pollJob = null
        } else {
            startPolling()
        }
    }

    private fun startPolling() {
        if (pollJob?.isActive == true) return
        pollJob = scope.launch {
            while (isActive) {
                delay(POLL_INTERVAL_MS)
                try {
                    val response = BackendApi.connectors()
                    val connectors = response.connectors.map { dto ->
                        Connector(
                            id = dto.id.toString(),
                            name = dto.name,
                            type = dto.type,
                            status = dto.status,
                        )
                    }
                    onConnectorsLoaded(connectors, response.canAdd)
                    if (connectors.allActive()) return@launch
                } catch (_: Throwable) {
                    // Продолжаем пуллинг при временных ошибках
                }
            }
        }
    }

    /**
     * Ставит коннектор на паузу (PATCH status = PAUSED) или возобновляет (status = ACTIVE).
     */
    fun setConnectorStatus(id: String, status: String) {
        scope.launch {
            val current = _state.value
            if (current !is ConnectorsViewState.ConnectorsData.Connectors) return@launch
            try {
                BackendApi.updateConnectorStatus(id, status)
                loadConnectors()
            } catch (e: Throwable) {
                val errorMsg = e.toUserFriendlyMessage("Ошибка при изменении статуса коннектора")
                _state.value = if (errorMsg.isUnauthError()) {
                    ConnectorsViewState.GotoAuth
                } else {
                    ConnectorsViewState.Error(errorMsg)
                }
            }
        }
    }

    /**
     * Удаляет коннектор по id через DELETE /api/v1/connectors/{id}.
     * При успехе обновляет список, при ошибке — обновляет [state].
     * Запускается в scope ViewModel.
     */
    fun deleteConnector(id: String) {
        scope.launch {
            val current = _state.value
            if (current !is ConnectorsViewState.ConnectorsData.Connectors) return@launch
            try {
                BackendApi.deleteConnector(id)
                loadConnectors()
            } catch (e: Throwable) {
                val errorMsg = e.toUserFriendlyMessage("Ошибка при удалении коннектора")
                _state.value = if (errorMsg.isUnauthError()) {
                    ConnectorsViewState.GotoAuth
                } else {
                    ConnectorsViewState.Error(errorMsg)
                }
            }
        }
    }

    /**
     * Создаёт URL-коннектор через POST /api/v1/connectors (JSON).
     * Запускается в scope ViewModel, чтобы не отменяться при уходе AddConnector-блока из композиции.
     */
    fun addUrlConnector(
        name: String,
        url: String,
        recursive: Boolean = true,
        onComplete: () -> Unit = {},
    ) {
        if (name.isBlank()) {
            _state.value = ConnectorsViewState.Error("Введите название коннектора")
            return
        }
        if (url.isBlank()) {
            _state.value = ConnectorsViewState.Error("Введите URL")
            return
        }
        scope.launch {
            try {
                addUrlConnectorSuspend(name, url.trim(), recursive)
            } finally {
                onComplete()
            }
        }
    }

    private suspend fun addUrlConnectorSuspend(
        name: String,
        url: String,
        recursive: Boolean,
    ) {
        try {
            BackendApi.createUrlConnector(name, url, recursive)
            loadConnectors()
        } catch (e: Throwable) {
            val errorMsg = e.toUserFriendlyMessage("Ошибка при добавлении коннектора")
            _state.value = if (errorMsg.isUnauthError()) {
                ConnectorsViewState.GotoAuth
            } else {
                ConnectorsViewState.Error(errorMsg)
            }
        }
    }

    /**
     * Создаёт file-коннектор через POST /api/v1/connectors.
     * Запускается в scope ViewModel, чтобы не отменяться при уходе AddConnector-блока из композиции.
     */
    fun addConnector(
        name: String,
        files: List<ByteArray>,
        filenames: List<String>,
        onComplete: () -> Unit = {},
    ) {
        if (files.isEmpty()) {
            _state.value = ConnectorsViewState.Error("Выберите хотя бы один файл")
            return
        }
        scope.launch {
            try {
                addConnectorSuspend(name, files, filenames)
            } finally {
                onComplete()
            }
        }
    }

    private suspend fun addConnectorSuspend(
        name: String,
        files: List<ByteArray>,
        filenames: List<String>,
    ) {
        try {
            BackendApi.createFileConnector(name, files, filenames)
            loadConnectors()
        } catch (e: Throwable) {
            val errorMsg = e.toUserFriendlyMessage("Ошибка при добавлении коннектора")
            _state.value = if (errorMsg.isUnauthError()) {
                ConnectorsViewState.GotoAuth
            } else {
                ConnectorsViewState.Error(errorMsg)
            }
        }
    }
}
