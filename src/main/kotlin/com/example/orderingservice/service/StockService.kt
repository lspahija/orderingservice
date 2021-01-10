package com.example.orderingservice.service

import com.example.orderingservice.config.ProductConfig
import com.example.orderingservice.listener.OrderEventListener
import org.springframework.stereotype.Service

@Service
class StockService(productConfig: ProductConfig, private val orderListeners: List<OrderEventListener>) {

    private val productToStock = productConfig.stock.toMutableMap()

    fun pickIfAdequateStockAvailable(productToQuantityOrdered: Map<String, Int>): Boolean {
        if (!allProductsAdequatelyStock(productToQuantityOrdered)) return false

        productToQuantityOrdered.forEach { productToStock[it.key] = productToStock[it.key]!! - it.value }
        return true
    }

    fun allProductsAdequatelyStock(productToQuantityOrdered: Map<String, Int>): Boolean {
        val productWithInadequateStockToQuantity = productToQuantityOrdered
            .filter { it.value > productToStock[it.key]!! }

        if (productWithInadequateStockToQuantity.isNotEmpty()) {
            val message = productWithInadequateStockToQuantity
                .entries.joinToString(prefix = "Order failed. The following products aren't available in the quantities you wish to purchase: ") {
                    "${productToStock[it.key]} units of item \"${it.key}\" are available in stock but you attempted to purchase ${it.value} units."
                }

            orderListeners.forEach { it.onOrderFailed(message) }
            return false
        }

        return true
    }

}