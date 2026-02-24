package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConnectorDto(
    val id: Int,
    val name: String,
    val type: String,
    val status: String? = null,
)
