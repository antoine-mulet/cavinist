package com.amulet.cavinist.persistence.repository.user

import com.amulet.cavinist.persistence.data.user.UserEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface CrudUserRepository : ReactiveCrudRepository<UserEntity, UUID> {

    fun findByLogin(login: String): Mono<UserEntity>
}