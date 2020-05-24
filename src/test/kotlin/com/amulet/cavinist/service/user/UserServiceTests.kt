package com.amulet.cavinist.service.user

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.user.*
import com.amulet.cavinist.persistence.repository.user.UserRepository
import com.amulet.cavinist.security.PasswordUtils
import com.amulet.cavinist.security.PasswordUtils.Companion.LoginPasswordValidationResult.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.*
import io.kotest.matchers.nulls.beNull
import io.mockk.*
import reactor.core.publisher.Mono
import java.util.UUID

class UserServiceTests : WordSpecUnitTests() {

    private val userRepository = mockk<UserRepository>()
    private val passwordHashUtils = mockk<PasswordUtils>()
    private val entityFactory = mockk<UserEntityFactory>()
    private val service = UserService(userRepository, passwordHashUtils, entityFactory)

    init {

        "getUserById" should {

            val userId = UUID.randomUUID()

            "return the correct user from the repository" {
                val user = UserEntity(userId, "login", "password")
                every { userRepository.findById(userId) } returns Mono.just(user)
                service.getUserById(userId) shouldBe user
            }

            "return null when the user doesn't exist" {
                every { userRepository.findById(userId) } returns Mono.empty()
                service.getUserById(userId) should beNull()
            }
        }

        "getUserByLogin" should {

            val userId = UUID.randomUUID()

            "return the correct user from the repository" {
                val user = UserEntity(userId, "login", "password")
                every { userRepository.findByLogin("login") } returns Mono.just(user)
                service.getUserByLogin("login") shouldBe user
            }

            "return null when the user doesn't exist" {
                every { userRepository.findByLogin("login") } returns Mono.empty()
                service.getUserByLogin("login") should beNull()
            }
        }

        "createUser" should {

            val login = "login"
            val password = "password"

            "create a new user with their password properly hashed" {
                val passwordHash = "hash"
                val newUser = mockk<UserEntity>()
                val expectedUser = UserEntity(UUID.randomUUID(), login, passwordHash, isNew = false)
                every { passwordHashUtils.validateLoginPassword(login, password) } returns ValidLoginPassword
                every { passwordHashUtils.hashPassword(password) } returns passwordHash
                every { entityFactory.newUser(login, passwordHash) } returns newUser
                every { userRepository.save(newUser) } returns Mono.just(expectedUser)
                service.createUser(login, password) shouldBe expectedUser
            }

            "fail when the login validation fails" {
                every { passwordHashUtils.validateLoginPassword(login, password) } returns LoginPolicyFailure("oops")
                shouldThrow<LoginPolicyFailureException> { service.createUser(login, password) }
            }

            "fail when the password validation fails" {
                every { passwordHashUtils.validateLoginPassword(login, password) } returns PasswordPolicyFailure("oops")
                shouldThrow<PasswordPolicyFailureException> { service.createUser(login, password) }
            }
        }

    }
}