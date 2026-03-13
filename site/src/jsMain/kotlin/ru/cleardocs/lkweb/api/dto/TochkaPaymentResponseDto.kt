package ru.cleardocs.lkweb.api.dto

import kotlinx.serialization.Serializable

/** Ответ POST /api/v1/pay/tochka/createPayment (TochkaPaymentResponseDto в Swagger). */
@Serializable
data class TochkaPaymentResponseDto(
    val paymentUrl: String,
)
