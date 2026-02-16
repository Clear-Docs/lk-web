package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetPlansDto(
    val plans: List<PlanDto>,
)
