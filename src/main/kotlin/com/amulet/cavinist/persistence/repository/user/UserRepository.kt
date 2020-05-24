package com.amulet.cavinist.persistence.repository.user

import com.amulet.cavinist.persistence.data.user.UserEntity
import com.amulet.cavinist.persistence.repository.CrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class UserRepository(override val crudRepository: CrudUserRepository) : CrudRepository<UserEntity>() {

    fun findByLogin(login: String): Mono<UserEntity> = crudRepository.findByLogin(login)
}