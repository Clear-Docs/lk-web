package ru.cleardocs.lkweb.plans

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.cleardocs.lkweb.api.BackendApi
import ru.cleardocs.lkweb.utils.toUserFriendlyMessage

class PlansViewModel(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {

    private val _plans = MutableStateFlow<List<Plan>>(emptyList())

    init {
        scope.launch { loadPlans() }
    }
    val plans: StateFlow<List<Plan>> = _plans.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _unsubscribeLoading = MutableStateFlow(false)
    val unsubscribeLoading: StateFlow<Boolean> = _unsubscribeLoading.asStateFlow()

    private val _unsubscribeError = MutableStateFlow<String?>(null)
    val unsubscribeError: StateFlow<String?> = _unsubscribeError.asStateFlow()

    /**
     * Отписка от подписки Точка Банк. После успеха перезагружает список тарифов.
     */
    fun unsubscribe() {
        scope.launch {
            _unsubscribeLoading.value = true
            _unsubscribeError.value = null
            try {
                BackendApi.unsubscribeTochka()
                loadPlans()
            } catch (e: Throwable) {
                _unsubscribeError.value = e.toUserFriendlyMessage("Ошибка отписки")
            } finally {
                _unsubscribeLoading.value = false
            }
        }
    }

    /**
     * Загружает список тарифов из REST GET /api/v1/plans и обновляет [plans].
     * Текущий тариф пользователя (для [Plan.isActive]) берётся из GET /api/v1/users/me.
     * Перед запросом выставляет [loading], при ошибке — [error].
     */
    suspend fun loadPlans() {
        _loading.value = true
        _error.value = null
        try {
            val currentPlanCode = try {
                BackendApi.me().plan.code
            } catch (_: Throwable) {
                null
            }
            val response = BackendApi.plans()
            _plans.value = response.plans.map { dto ->
                Plan(
                    code = dto.code,
                    title = dto.title,
                    isActive = dto.code == currentPlanCode,
                    priceRub = dto.priceRub,
                    periodDays = dto.periodDays,
                    limit = dto.limit,
                )
            }
        } catch (e: Throwable) {
            _error.value = e.toUserFriendlyMessage("Ошибка загрузки тарифов")
            _plans.value = emptyList()
        } finally {
            _loading.value = false
        }
    }
}
