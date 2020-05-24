package com.amulet.cavinist.persistence.data.user

import org.springframework.stereotype.Component
import java.util.UUID

interface UserEntityFactory {

    fun newUser(login: String, passwordHash: String): UserEntity

}

@Component
object UserEntityFactoryImpl : UserEntityFactory {

    override fun newUser(login: String, passwordHash: String): UserEntity =
        UserEntity(UUID.randomUUID(), login, passwordHash, isNew = true)

}