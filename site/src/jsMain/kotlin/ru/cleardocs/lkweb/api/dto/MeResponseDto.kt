package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

/** Ответ GET /api/v1/users/me — обёртка с ключом "user". */
@Serializable
data class MeResponseDto(
    val user: UserMeDto,
)

@Serializable
data class UserMeDto(
    val id: String? = null,
    val email: String? = null,
    val name: String? = null,
    val plan: PlanDto? = null,
)
