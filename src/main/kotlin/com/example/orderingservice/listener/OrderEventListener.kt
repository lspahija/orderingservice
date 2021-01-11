package com.example.orderingservice.listener

import com.example.orderingservice.event.OrderEvent

interface OrderEventListener {
    fun onSubmitted(products: Array<String>)
    fun onProcessed(order: OrderEvent)
    fun onOrderFailed(message: String)
}