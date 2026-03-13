package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

/** Тело POST /api/v1/pay/tochka/createPayment (TochkaPaymentRequestDto в Swagger). */
@Serializable
data class TochkaPaymentRequestDto(
    val planCode: String,
)
