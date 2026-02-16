package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class MeDto(
    val id: String,
    val email: String? = null,
)
