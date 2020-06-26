package com.hyecheon.payapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class PayApiApplication

fun main(args: Array<String>) {
	runApplication<PayApiApplication>(*args)
}
