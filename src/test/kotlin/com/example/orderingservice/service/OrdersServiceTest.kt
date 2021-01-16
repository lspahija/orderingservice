package com.example.orderingservice.service

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class OrdersServiceTest {

    @Autowired
    private lateinit var ordersService: OrdersService

    @MockBean
    private lateinit var kafkaTemplate: KafkaTemplate<Any, Any>

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
        ordersService.order(
            arrayOf(
                "apple",
                "orange",
                "apple",
                "apple",
                "orange",
                "orange",
                "orange",
                "orange",
                "orange"
            )
        )

        val logMessageIterator = listAppender.list.iterator()

        assertEquals(3, listAppender.list.size)
        assertEquals("subtotal: 3.30", logMessageIterator.next().message)
        assertEquals("discount: 1.10", logMessageIterator.next().message)
        assertEquals("total: 2.20", logMessageIterator.next().message)
    }

    @Test
    fun testMultipleOrders() {
        ordersService.order(arrayOf("apple", "orange", "apple", "apple", "orange"))
        ordersService.order(arrayOf("apple", "orange"))

        val logMessageIterator = listAppender.list.iterator()
        val getNextMessage = { logMessageIterator.next().message }

        assertEquals(6, listAppender.list.size)
        assertEquals("subtotal: 2.30", getNextMessage())
        assertEquals("discount: 0.60", getNextMessage())
        assertEquals("total: 1.70", getNextMessage())
        assertEquals("subtotal: 0.85", getNextMessage())
        assertEquals("discount: 0.00", getNextMessage())
        assertEquals("total: 0.85", getNextMessage())
    }

    @Test
    fun testEmptyOrder() {
        ordersService.order(arrayOf())

        val logMessageIterator = listAppender.list.iterator()

        assertEquals(3, listAppender.list.size)
        assertEquals("subtotal: 0", logMessageIterator.next().message)
        assertEquals("discount: 0", logMessageIterator.next().message)
        assertEquals("total: 0", logMessageIterator.next().message)
    }

    @Test
    fun testNonExistingProduct() {
        assertThrows<IllegalArgumentException> {
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