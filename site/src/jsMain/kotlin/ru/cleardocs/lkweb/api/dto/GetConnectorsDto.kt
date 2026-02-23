package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetConnectorsDto(
    val connectors: List<ConnectorDto>,
)
