package ru.cleardocs.lkweb.plans

import ru.cleardocs.lkweb.api.dto.LimitDto

/**
 * View model для тарифа — только данные для отображения.
 * Маппинг из [PlanDto] выполняется во ViewModel.
 */
data class Plan(
    val code: String,
    val title: String,
    val isActive: Boolean,
    val priceRub: Int,
    val periodDays: Int,
    val limit: LimitDto,
)
