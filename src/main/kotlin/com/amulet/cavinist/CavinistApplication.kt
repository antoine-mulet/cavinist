package com.amulet.cavinist

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CavinistApplication

fun main(args: Array<String>) {
	runApplication<CavinistApplication>(*args)
}
