package com.example.orderingservice.event

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class OrderEvent(
    val uuid: UUID,
    val products: List<String>,
    val total: BigDecimal,
    val discount: BigDecimal,
    val orderTime: LocalDateTime,
    val estimatedDeliveryDate: LocalDate
    )
