package com.amulet.cavinist.service.auth

import com.amulet.cavinist.security.*
import com.amulet.cavinist.service.user.UserService
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    val userService: UserService,
    val passwordUtils: PasswordUtils,
    val jwtUtils: JwtUtils) {

    companion object Companion {
        private const val dummyHash =
            "\$argon2id\$v=19\$m=32768,t=10,p=2\$Zz2DgYJhg7+n43vl27uEIg$\r2J3Qs0X2/ox3L+k1IDp6BSNlL+5rial1XzU/N8+Lzw"
    }

    suspend fun login(login: String, password: String): UserLoginResult {
        val user = userService.getUserByLogin(login)
        val userPassword = user?.passwordHash ?: dummyHash // we check a dummy password even when the user doesn't exist to prevent timing attack
        if (passwordUtils.checkPassword(password, userPassword) && user != null)
            return UserLoginResult(jwtUtils.issueJwtForUser(user.ID), user)
        else
            throw AuthenticationFailureException("Login or password is incorrect.")
    }

}