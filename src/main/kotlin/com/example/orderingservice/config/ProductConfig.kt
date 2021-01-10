package com.example.orderingservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.math.BigDecimal

@ConstructorBinding
@ConfigurationProperties("product")
data class ProductConfig(val price: Map<String, BigDecimal>)
