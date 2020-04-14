package com.amulet.cavinist

import com.amulet.cavinist.web.hooks.CustomSchemaGeneratorHooks
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    @Bean
    fun hooks() = CustomSchemaGeneratorHooks()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
