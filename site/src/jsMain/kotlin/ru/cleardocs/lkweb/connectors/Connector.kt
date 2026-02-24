package ru.cleardocs.lkweb.connectors

/**
 * Доменная модель коннектора для отображения.
 */
data class Connector(
    val id: String,
    val name: String,
    val type: String,
    val status: String? = null,
)
