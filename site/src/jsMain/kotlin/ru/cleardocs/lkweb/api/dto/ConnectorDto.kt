package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConnectorDto(
    val id: String,
    val name: String,
    val type: String,
)
