package com.amulet.cavinist.persistence

import com.amulet.cavinist.persistence.data.*
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.transaction.ReactiveTransactionManager

@Configuration
@EnableR2dbcRepositories(basePackages = ["crud"])
class R2dbcConfig : AbstractR2dbcConfiguration() {

    @Autowired
    @Qualifier("connectionFactory")
    lateinit var connectionFactory: ConnectionFactory

    override fun connectionFactory(): ConnectionFactory {
        return connectionFactory
    }

    @Bean
    fun transactionManager(
        @Qualifier("connectionFactory")
        connectionFactory: ConnectionFactory): ReactiveTransactionManager? {
        return R2dbcTransactionManager(connectionFactory)
    }

    @Bean
    override fun r2dbcCustomConversions(): R2dbcCustomConversions {
        val converterList = listOf(WineTypeToIntegerConverter, IntegerToWineTypeConverter)
        return R2dbcCustomConversions(storeConversions, converterList)
    }
}