package com.amulet.cavinist.service.user

import com.amulet.cavinist.persistence.data.user.*
import com.amulet.cavinist.persistence.repository.user.UserRepository
import com.amulet.cavinist.security.PasswordUtils
import com.amulet.cavinist.security.PasswordUtils.Companion.LoginPasswordValidationResult.*
import com.amulet.cavinist.web.data.input.InvalidInputDataException
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    val repository: UserRepository,
    val passwordUtils: PasswordUtils,
    val entityFactory: UserEntityFactory) {

    suspend fun getUserById(userId: UUID): UserEntity? = repository.findById(userId).awaitFirstOrNull()

    suspend fun createUser(login: String, password: String): UserEntity? {
        val newUser = when (val validationResult = passwordUtils.validateLoginPassword(login, password)) {
            is ValidLoginPassword    -> entityFactory.newUser(login, passwordUtils.hashPassword(password))
            is LoginPolicyFailure    -> throw LoginPolicyFailureException(validationResult.policyDescription)
            is PasswordPolicyFailure -> throw PasswordPolicyFailureException(validationResult.policyDescription)
        }
        return repository.save(newUser).awaitFirstOrNull()
    }

    suspend fun getUserByLogin(login: String): UserEntity? = repository.findByLogin(login).awaitFirstOrNull()
}