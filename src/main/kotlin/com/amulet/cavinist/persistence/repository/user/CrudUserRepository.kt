package com.amulet.cavinist.persistence.repository.user

import com.amulet.cavinist.persistence.data.user.UserEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CrudUserRepository : CoroutineCrudRepository<UserEntity, UUID> {

    suspend fun findByLogin(login: String): UserEntity?
}