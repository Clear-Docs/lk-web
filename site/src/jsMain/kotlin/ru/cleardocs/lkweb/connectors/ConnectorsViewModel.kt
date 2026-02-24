package ru.cleardocs.lkweb.connectors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.cleardocs.lkweb.api.BackendApi

class ConnectorsViewModel(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {

    private val _state = MutableStateFlow<ConnectorsViewState>(ConnectorsViewState.Loading)
    val state: StateFlow<ConnectorsViewState> = _state.asStateFlow()

    private var lastConnectors: List<Connector> = emptyList()
    private var lastCanAdd: Boolean = true

    init {
        scope.launch { loadConnectors() }
    }

    fun goToAddFile() {
        val current = _state.value
        if (current is ConnectorsViewState.ConnectorsData.Connectors) {
            lastConnectors = current.connectors
            lastCanAdd = current.canAdd
            _state.value = ConnectorsViewState.ConnectorsData.AddFile
        }
    }

    fun backToConnectors() {
        _state.value = ConnectorsViewState.ConnectorsData.Connectors(lastConnectors, lastCanAdd)
    }

    private fun isUnauthError(error: String): Boolean =
        error.contains("401") || error.contains("403") ||
            error.contains("Сессия истекла") || error.contains("Доступ запрещён") ||
            error.contains("User not authenticated", ignoreCase = true) ||
            error.contains("не авторизован", ignoreCase = true)

    /**
     * Загружает список коннекторов из REST GET /api/v1/connectors.
     * Токен получается в BackendApi из Firebase Auth.
     */
    suspend fun loadConnectors() {
        _state.value = ConnectorsViewState.Loading
        try {
            val response = BackendApi.connectors()
            val connectors = response.connectors.map { dto ->
                Connector(
                    id = dto.id.toString(),
                    name = dto.name,
                    type = dto.type,
                )
            }
            _state.value = ConnectorsViewState.ConnectorsData.Connectors(connectors, response.canAdd)
        } catch (e: Throwable) {
            val errorMsg = when {
                e is ru.cleardocs.lkweb.api.BackendError -> when (e.code) {
                    401 -> "Сессия истекла"
                    403 -> "Доступ запрещён"
                    else -> e.message ?: "Ошибка ${e.code}"
                }
                e.message?.contains("401") == true -> "Сессия истекла"
                e.message?.contains("403") == true -> "Доступ запрещён"
                e.message?.contains("Backend unreachable") == true -> "Сервер недоступен"
                else -> e.message ?: "Ошибка загрузки коннекторов"
            }
            _state.value = if (isUnauthError(errorMsg)) {
                ConnectorsViewState.GotoAuth
            } else {
                ConnectorsViewState.Error(errorMsg)
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
                val errorMsg = when {
                    e is ru.cleardocs.lkweb.api.BackendError -> when (e.code) {
                        401 -> "Сессия истекла"
                        403 -> "Доступ запрещён"
                        else -> e.message ?: "Ошибка ${e.code}"
                    }
                    e.message?.contains("401") == true -> "Сессия истекла"
                    e.message?.contains("403") == true -> "Доступ запрещён"
                    e.message?.contains("Backend unreachable") == true -> "Сервер недоступен"
                    else -> e.message ?: "Ошибка при удалении коннектора"
                }
                _state.value = if (isUnauthError(errorMsg)) {
                    ConnectorsViewState.GotoAuth
                } else {
                    ConnectorsViewState.Error(errorMsg)
                }
            }
        }
    }

    /**
     * Создаёт file-коннектор через POST /api/v1/connectors.
     * При успехе обновляет список, при ошибке — обновляет [state].
     */
    suspend fun addConnector(
        name: String,
        files: List<ByteArray>,
        filenames: List<String>,
    ) {
        if (files.isEmpty()) {
            _state.value = ConnectorsViewState.Error("Выберите хотя бы один файл")
            return
        }
        try {
            BackendApi.createFileConnector(name, files, filenames)
            loadConnectors()
        } catch (e: Throwable) {
            val errorMsg = when {
                e is ru.cleardocs.lkweb.api.BackendError -> when (e.code) {
                    401 -> "Сессия истекла"
                    403 -> "Доступ запрещён"
                    else -> e.message ?: "Ошибка ${e.code}"
                }
                e.message?.contains("401") == true -> "Сессия истекла"
                e.message?.contains("403") == true -> "Доступ запрещён"
                e.message?.contains("Backend unreachable") == true -> "Сервер недоступен"
                else -> e.message ?: "Ошибка при добавлении коннектора"
            }
            _state.value = if (isUnauthError(errorMsg)) {
                ConnectorsViewState.GotoAuth
            } else {
                ConnectorsViewState.Error(errorMsg)
            }
        }
    }
}
