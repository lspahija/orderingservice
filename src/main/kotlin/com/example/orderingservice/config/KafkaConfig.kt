package com.example.orderingservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter

@Configuration
class KafkaConfig {

    @Bean
    fun converter(): RecordMessageConverter {
        return StringJsonMessageConverter()
    }
}