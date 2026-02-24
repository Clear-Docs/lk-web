package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

/** Тело PATCH /api/v1/connectors/{connectorId} (UpdateConnectorRequestDto в Swagger). */
@Serializable
data class UpdateConnectorRequestDto(
    val status: String? = null,
    val paused: Boolean? = null,
    val active: Boolean? = null,
)
