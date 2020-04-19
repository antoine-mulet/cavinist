package com.amulet.cavinist

import com.amulet.cavinist.web.graphql.*
import graphql.execution.DataFetcherExceptionHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    @Bean
    fun hooks() = CustomSchemaGeneratorHooks()

    @Bean
    fun exceptionHandler(): DataFetcherExceptionHandler = CustomDataFetcherExceptionHandler()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
