package ru.cleardocs.lkweb.plans

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.cleardocs.lkweb.api.BackendApi
import ru.cleardocs.lkweb.api.dto.PlanDto

class PlansViewModel {

    private val _plans = MutableStateFlow<List<PlanDto>>(emptyList())
    val plans: StateFlow<List<PlanDto>> = _plans.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Загружает список тарифов из REST GET /api/v1/plans и обновляет [plans].
     * Перед запросом выставляет [loading], при ошибке — [error].
     */
    suspend fun loadPlans() {
        _loading.value = true
        _error.value = null
        try {
            val response = BackendApi.plans()
            _plans.value = response.plans
        } catch (e: Throwable) {
            _error.value = e.message ?: "Ошибка загрузки тарифов"
            _plans.value = emptyList()
        } finally {
            _loading.value = false
        }
    }
}
