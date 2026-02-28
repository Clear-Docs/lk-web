package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

/** Тело POST /api/v1/connectors для URL-коннектора (Content-Type: application/json). */
@Serializable
data class CreateUrlConnectorRequestDto(
    val name: String,
    val url: String,
    val recursive: Boolean = true,
)
