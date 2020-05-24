package com.amulet.cavinist.service.auth

import com.amulet.cavinist.common.WordSpecUnitTests
import com.amulet.cavinist.persistence.data.user.UserEntity
import com.amulet.cavinist.security.*
import com.amulet.cavinist.service.user.UserService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.UUID

class AuthenticationServiceTests : WordSpecUnitTests() {

    private val userService = mockk<UserService>()
    private val passwordHashUtils = mockk<PasswordUtils>()
    private val jwtUtils = mockk<JwtUtils>()
    private val service = AuthenticationService(userService, passwordHashUtils, jwtUtils)

    init {

        "login" should {

            val login = "login"
            val password = "password"
            val passwordHash = "hash"

            "create a JWT when an existing user with the correct password logs in" {
                val userId = UUID.randomUUID()
                val user = UserEntity(userId, login, passwordHash)
                coEvery { userService.getUserByLogin(login) } returns user
                every { passwordHashUtils.checkPassword(password, passwordHash) } returns true
                every { jwtUtils.issueJwtForUser(userId) } returns "jwt"
                service.login(login, password) shouldBe UserLoginResult("jwt", user)
            }

            "fail when the password doesn't match" {
                val userId = UUID.randomUUID()
                val user = UserEntity(userId, login, passwordHash)
                coEvery { userService.getUserByLogin(login) } returns user
                every { passwordHashUtils.checkPassword(password, passwordHash) } returns false
                shouldThrow<AuthenticationFailureException> { service.login(login, password) }
            }

            "fail but still check a fake password when no user exists for this login" {
                coEvery { userService.getUserByLogin(login) } returns null
                every { passwordHashUtils.checkPassword(password, any()) } returns true
                shouldThrow<AuthenticationFailureException> { service.login(login, password) }
            }

        }

    }

}