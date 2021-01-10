package com.example.orderingservice.service

import com.example.orderingservice.event.OrderEvent
import com.example.orderingservice.listener.OrderEventListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MailService : OrderEventListener {

    private val logger = LoggerFactory.getLogger(MailService::class.java)

    override fun onProcessed(order: OrderEvent) =
        logger.info(
            "Your order has been successfully processed " +
                    "and its estimated delivery date is ${order.estimatedDeliveryDate}."
        )
}