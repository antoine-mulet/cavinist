package com.amulet.cavinist.persistence.repository

import com.amulet.cavinist.persistence.data.Entity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

abstract class CrudCoroutineRepository<T : Entity> {

    abstract val crudRepository: CoroutineCrudRepository<T, UUID>

    suspend fun save(entity: T): T {
        return try {
            crudRepository.save(entity)
        } catch (exception: Throwable) {
            throw when (exception) {
                is DataIntegrityViolationException ->
                    DataIntegrityViolationException("${entity.description()} already exists.")

                is OptimisticLockingFailureException ->
                    OutdatedVersionException("Failed to update ${entity.name()} with id '${entity.id}' because version is outdated.")

                else -> exception
            }
        }
    }

    suspend fun findById(id: UUID): T? = crudRepository.findById(id)
}