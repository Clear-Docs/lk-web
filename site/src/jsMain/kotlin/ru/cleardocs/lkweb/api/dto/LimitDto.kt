package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class LimitDto(
    val maxConnectors: Int,
)
