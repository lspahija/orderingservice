package com.example.orderingservice.listener

import com.example.orderingservice.event.OrderEvent

interface OrderEventListener {
    fun onProcessed(order: OrderEvent)
    fun onOrderFailed(message: String)
}