package com.amulet.cavinist.persistence.user

import com.amulet.cavinist.common.WordSpecIT
import com.amulet.cavinist.persistence.data.user.UserEntity
import com.amulet.cavinist.persistence.repository.user.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.haveMessage
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import java.util.UUID

class UserRepositoryIT : WordSpecIT() {

    @Autowired
    private lateinit var repository: UserRepository

    init {

        "findById" should {
            "return the correct user when it exists" {
                runBlocking { repository.findById(dataSet.userOne.ID) } shouldBe dataSet.userOne
            }

            "return null when the user doesn't exists" {
                runBlocking { repository.findById(UUID.randomUUID()) } shouldBe null
            }
        }

        "findByLogin" should {
            "return the correct user when it exists" {
                runBlocking { repository.findByLogin(dataSet.userOne.login) } shouldBe dataSet.userOne
            }

            "return null when the user doesn't exists" {
                runBlocking { repository.findByLogin("unknown") } shouldBe null
            }
        }

        "save" should {
            "save a new user" {
                val newUser = UserEntity(UUID.randomUUID(), "login", "password", true)
                val res = runBlocking { repository.save(newUser) }
                runBlocking { repository.findById(res.ID) } shouldBe newUser.copy(isNew = false)
            }

            "update an existing user" {
                val updatedUser = dataSet.userOne.copy(passwordHash = "P@sSw0rD")
                runBlocking { repository.save(updatedUser) } shouldBe updatedUser
            }

            "fail properly when trying to save a user with the same login as an already existing user" {
                val exception = shouldThrow<DataIntegrityViolationException> {
                    runBlocking { repository.save(dataSet.userOne.copy(passwordHash = "password", isNew = true)) }
                }
                exception should haveMessage("User with login '${dataSet.userOne.login}' already exists.")
            }
        }

    }
}