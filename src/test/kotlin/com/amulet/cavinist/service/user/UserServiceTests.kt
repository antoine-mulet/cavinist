package com.amulet.cavinist.service.user

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.user.UserEntity
import com.amulet.cavinist.persistence.data.user.UserEntityFactory
import com.amulet.cavinist.persistence.repository.user.UserRepository
import com.amulet.cavinist.security.PasswordUtils
import com.amulet.cavinist.security.PasswordUtils.Companion.LoginPasswordValidationResult.LoginPolicyFailure
import com.amulet.cavinist.security.PasswordUtils.Companion.LoginPasswordValidationResult.PasswordPolicyFailure
import com.amulet.cavinist.security.PasswordUtils.Companion.LoginPasswordValidationResult.ValidLoginPassword
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
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
                coEvery { userRepository.findById(userId) } returns user
                service.getUserById(userId) shouldBe user
            }

            "return null when the user doesn't exist" {
                coEvery { userRepository.findById(userId) } returns null
                service.getUserById(userId) should beNull()
            }
        }

        "getUserByLogin" should {

            val userId = UUID.randomUUID()

            "return the correct user from the repository" {
                val user = UserEntity(userId, "login", "password")
                coEvery { userRepository.findByLogin("login") } returns user
                service.getUserByLogin("login") shouldBe user
            }

            "return null when the user doesn't exist" {
                coEvery { userRepository.findByLogin("login") } returns null
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
                coEvery { userRepository.save(newUser) } returns expectedUser
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