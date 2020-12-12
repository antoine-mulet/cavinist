package com.amulet.cavinist.utils

import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*

suspend fun <T> Mono<T>.suspending(): T? = this.awaitFirstOrNull()

suspend fun <T> Flux<T>.suspending(): List<T> = this.collectList().awaitSingle()