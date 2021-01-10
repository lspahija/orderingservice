package com.example.orderingservice

import com.example.orderingservice.config.ProductConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ProductConfig::class)
class OrderingserviceApplication

fun main(args: Array<String>) {
    runApplication<OrderingserviceApplication>(*args)
}
