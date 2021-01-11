package com.example.orderingservice.service

import com.example.orderingservice.config.DiscountKey
import com.example.orderingservice.config.ProductConfig
import com.example.orderingservice.event.OrderEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ShellComponent
class OrdersService(
    private val productConfig: ProductConfig,
    private val stockService: StockService,
    private val kafkaTemplate: KafkaTemplate<Any, Any>
) {

    private val logger = LoggerFactory.getLogger(OrdersService::class.java)
    private val orders = mutableSetOf<OrderEvent>()

    @ShellMethod("Order products")
    fun order(@ShellOption products: Array<String>) {
        kafkaTemplate.send("OrderSubmitted", products)

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

        processOrder(normalized, productToQuantity, total, discount)
    }

    fun getDiscount(product: String, quantity: Int): BigDecimal {
        val discountCriteria = productConfig.discount[product] ?: return BigDecimal.ZERO
        val purchaseQuantityCriterion = discountCriteria[DiscountKey.PURCHASE]!!
        val timesDiscountCriterionSatisfied = quantity / purchaseQuantityCriterion
        val freeQuantityPerCriterionSatisfied = purchaseQuantityCriterion - discountCriteria[DiscountKey.CHARGE]!!
        val totalFreeQuantity = freeQuantityPerCriterionSatisfied * timesDiscountCriterionSatisfied
        return productConfig.price[product]!!.times(BigDecimal(totalFreeQuantity))
    }

    fun processOrder(
        products: List<String>,
        productToQuantityOrdered: Map<String, Int>,
        total: BigDecimal,
        discount: BigDecimal
    ) {
        if (!stockService.pickIfAdequateStockAvailable(productToQuantityOrdered)) return

        OrderEvent(
            UUID.randomUUID(),
            products,
            total,
            discount,
            LocalDateTime.now(),
            getEstimatedDeliveryDate()
        ).also {
            orders.add(it)
            notifyListenersOrderProcessed(it)
        }
    }

    fun notifyListenersOrderProcessed(orderEvent: OrderEvent) = kafkaTemplate.send("OrderProcessed", orderEvent)

    fun getEstimatedDeliveryDate(): LocalDate = (5..10).random().let { LocalDate.now().plusDays(it.toLong()) }
}