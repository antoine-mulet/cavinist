package com.amulet.cavinist.service

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator

@Component
class TxManager(@Qualifier("connectionFactory") connectionFactory: ConnectionFactory) {

    private val tm: ReactiveTransactionManager by lazy {
        R2dbcTransactionManager(connectionFactory)
    }

    private val rxtx by lazy { TransactionalOperator.create(tm) }

    suspend fun <T> newTx(block: suspend () -> T): T {
        return mono { block() }.`as`(rxtx::transactional).awaitFirst()
    }

}