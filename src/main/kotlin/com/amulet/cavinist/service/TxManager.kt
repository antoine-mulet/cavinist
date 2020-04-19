package com.amulet.cavinist.service

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Component
class TxManager(@Qualifier("connectionFactory") connectionFactory: ConnectionFactory) {

    private val tm: ReactiveTransactionManager by lazy {
        R2dbcTransactionManager(connectionFactory)
    }

    private val rxtx by lazy { TransactionalOperator.create(tm) }

    fun <T> newTx(block: Mono<T>): Mono<T> {
        return block.`as`(rxtx::transactional)
    }

}