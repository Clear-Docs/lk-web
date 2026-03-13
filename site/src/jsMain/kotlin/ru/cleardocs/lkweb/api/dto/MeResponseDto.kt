package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

/** Ответ GET /api/v1/users/me. Поле isCanceled приходит в корне ответа, не внутри user. */
@Serializable
data class MeResponseDto(
    val user: UserMeDto,
    val isCanceled: Boolean = false,
)

@Serializable
data class UserMeDto(
    val id: String? = null,
    val email: String? = null,
    val name: String? = null,
    val plan: PlanDto? = null,
    val docSetId: Int? = null,
)
