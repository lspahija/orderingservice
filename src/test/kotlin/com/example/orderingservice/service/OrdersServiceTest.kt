package com.example.orderingservice.service

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class OrdersServiceTest {

    @Autowired
    private lateinit var ordersService: OrdersService

    private val listAppender = ListAppender<ILoggingEvent>()

    @BeforeEach
    fun setUp() {
        val logger = LoggerFactory.getLogger(OrdersService::class.java.name) as Logger
        logger.addAppender(listAppender)
        logger.level = Level.INFO
        listAppender.start()
    }

    @AfterEach
    fun tearDown() {
        val logger = LoggerFactory.getLogger(OrdersService::class.java.name) as Logger
        logger.detachAppender(listAppender)
    }

    @Test
    fun testOrder() {
        ordersService.order(arrayOf("apple", "orange", "apple", "apple", "orange"))

        assertEquals(1, listAppender.list.size)
        assertEquals("total: 2.30", listAppender.list[0].message)
    }

    @Test
    fun testMultipleOrders() {
        ordersService.order(arrayOf("apple", "orange", "apple", "apple", "orange"))
        ordersService.order(arrayOf("apple", "orange"))

        assertEquals(2, listAppender.list.size)
        assertEquals("total: 2.30", listAppender.list[0].message)
        assertEquals("total: 0.85", listAppender.list[1].message)
    }

    @Test
    fun testEmptyOrder() {
        ordersService.order(arrayOf())

        assertEquals(1, listAppender.list.size)
        assertEquals("total: 0", listAppender.list[0].message)
    }

    @Test
    fun testNonExistingProduct() {
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            ordersService.order(
                arrayOf(
                    "apple",
                    "orange",
                    "pear"
                )
            )
        }
    }
}