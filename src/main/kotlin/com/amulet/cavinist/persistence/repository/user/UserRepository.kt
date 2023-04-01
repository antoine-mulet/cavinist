package com.amulet.cavinist.persistence.repository.user

import com.amulet.cavinist.persistence.data.user.UserEntity
import com.amulet.cavinist.persistence.repository.CrudCoroutineRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepository(override val crudRepository: CrudUserRepository) : CrudCoroutineRepository<UserEntity>() {

    suspend fun findByLogin(login: String): UserEntity? = crudRepository.findByLogin(login)
}