package com.example.orderingservice.service

import com.example.orderingservice.event.OrderEvent
import com.example.orderingservice.listener.OrderEventListener
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class MailService : OrderEventListener {

    private val logger = LoggerFactory.getLogger(MailService::class.java)

    @KafkaListener(id = "submitted", topics = ["OrderSubmitted"])
    override fun onSubmitted(products: Array<String>) =
        logger.info("Received order for the following products: ${products.contentToString()}")


    @KafkaListener(id = "processed", topics = ["OrderProcessed"])
    override fun onProcessed(order: OrderEvent) =
        logger.info(
            "Your order has been successfully processed " +
                    "and its estimated delivery date is ${order.estimatedDeliveryDate}."
        )

    @KafkaListener(id = "failed", topics = ["OrderFailed"])
    override fun onOrderFailed(message: String) =
        logger.info(message)
}