package com.example.orderingservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.math.BigDecimal

@ConstructorBinding
@ConfigurationProperties("product")
data class ProductConfig(
    val price: Map<String, BigDecimal>,
    val discount: Map<String, Map<DiscountKey, Int>>,
)

enum class DiscountKey { PURCHASE, CHARGE }
