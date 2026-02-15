package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlanDto(
    val code: String,
    val title: String,
    val priceRub: Int,
    val periodDays: Int,
    val limit: LimitDto,
)
