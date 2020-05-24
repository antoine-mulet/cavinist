package com.amulet.cavinist.persistence.repository

import com.amulet.cavinist.persistence.data.Entity
import org.springframework.dao.*
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.UUID

abstract class CrudRepository<T : Entity> {

    abstract val crudRepository: ReactiveCrudRepository<T, UUID>

    fun save(entity: T): Mono<T> =
        crudRepository.save(entity)
            .onErrorMap {
                when (it) {
                    is DataIntegrityViolationException   ->
                        DataIntegrityViolationException("${entity.description()} already exists.")
                    is OptimisticLockingFailureException ->
                        OutdatedVersionException("Failed to update ${entity.name()} with id '${entity.id}' because version is outdated.")
                    else                                 -> it
                }
            }

    fun findById(id: UUID): Mono<T> = crudRepository.findById(id)
}