package com.example.orderingservice.service

import com.example.orderingservice.config.DiscountKey
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
    fun order(@ShellOption products: Array<String>) {
        val normalized = products
            .map { it.toLowerCase() }
            .onEach { if (!productConfig.price.containsKey(it)) throw IllegalArgumentException("No product exists with name $it.") }

        val subTotal = normalized
            .fold(BigDecimal.ZERO) { total, item -> total + productConfig.price[item]!! }

        val productToQuantity = normalized.groupingBy { it }.eachCount()

        val discount = productToQuantity.entries
            .fold(BigDecimal.ZERO) { total, entry -> total + getDiscount(entry.key, entry.value) }

        val total = subTotal - discount

        logger.info("subtotal: $subTotal")
        logger.info("discount: $discount")
        logger.info("total: $total")
    }

    fun getDiscount(product: String, quantity: Int): BigDecimal {
        val discountCriteria = productConfig.discount[product] ?: return BigDecimal.ZERO
        val purchaseQuantityCriterion = discountCriteria[DiscountKey.PURCHASE]!!
        val timesDiscountCriterionSatisfied = quantity / purchaseQuantityCriterion
        val freeQuantityPerCriterionSatisfied = purchaseQuantityCriterion - discountCriteria[DiscountKey.CHARGE]!!
        val totalFreeQuantity = freeQuantityPerCriterionSatisfied * timesDiscountCriterionSatisfied
        return productConfig.price[product]!!.times(BigDecimal(totalFreeQuantity))
    }
}