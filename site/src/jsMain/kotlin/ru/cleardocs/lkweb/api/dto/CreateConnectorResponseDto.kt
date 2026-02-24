package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

/** Ответ POST /api/v1/connectors (createFileConnector). */
@Serializable
data class CreateConnectorResponseDto(
    val id: Int,
    val name: String,
    val type: String,
)
