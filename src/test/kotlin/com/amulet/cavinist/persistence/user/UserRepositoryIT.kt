package com.amulet.cavinist.persistence.user

import com.amulet.cavinist.common.WordSpecIT
import com.amulet.cavinist.persistence.data.user.UserEntity
import com.amulet.cavinist.persistence.repository.user.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.*
import io.kotest.matchers.throwable.haveMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import java.util.UUID

class UserRepositoryIT : WordSpecIT() {

    @Autowired
    private lateinit var repository: UserRepository

    init {

        "findById" should {
            "return the correct user when it exists" {
                repository.findById(dataSet.theChosenOne.ID).block() shouldBe dataSet.theChosenOne
            }

            "return null when the user doesn't exists" {
                repository.findById(UUID.randomUUID()).block() shouldBe null
            }
        }

        "findByLogin" should {
            "return the correct user when it exists" {
                repository.findByLogin(dataSet.theChosenOne.login).block() shouldBe dataSet.theChosenOne
            }

            "return null when the user doesn't exists" {
                repository.findByLogin("unknown").block() shouldBe null
            }
        }

        "save" should {
            "save a new user" {
                val newUser = UserEntity(UUID.randomUUID(), "login", "password", true)
                val res = repository.save(newUser).block()!!
                repository.findById(res.ID).block() shouldBe newUser.copy(isNew = false)
            }

            "update an existing user" {
                val updatedUser = dataSet.theChosenOne.copy(passwordHash = "P@sSw0rD")
                val res = repository.save(updatedUser).block()!!
                res shouldBe updatedUser
            }

            "fail properly when trying to save a user with the same login as an already existing user" {
                val exception = shouldThrow<DataIntegrityViolationException> {
                    repository.save(dataSet.theChosenOne.copy(passwordHash = "password", isNew = true)).block()!!
                }
                exception should haveMessage("User with login '${dataSet.theChosenOne.login}' already exists.")
            }
        }

    }
}