package com.example.orderingservice.service

import com.example.orderingservice.config.ProductConfig
import org.slf4j.LoggerFactory
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.math.BigDecimal

@ShellComponent
class OrdersService(private val productConfig: ProductConfig) {

    private val logger = LoggerFactory.getLogger(OrdersService::class.java)

    @ShellMethod("Order products")
    fun order(@ShellOption products: Array<String>) = products
        .map { it.toLowerCase() }
        .onEach { if (!productConfig.price.containsKey(it)) throw IllegalArgumentException("No product exists with name $it.") }
        .fold(BigDecimal.ZERO) { total, item -> total + productConfig.price[item]!! }
        .let { logger.info("total: $it") }

}