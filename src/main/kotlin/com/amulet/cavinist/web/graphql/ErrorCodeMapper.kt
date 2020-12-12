package com.amulet.cavinist.web.graphql

import com.amulet.cavinist.persistence.repository.OutdatedVersionException
import com.amulet.cavinist.service.ObjectNotFoundException
import com.amulet.cavinist.service.auth.AuthenticationFailureException
import com.amulet.cavinist.service.user.*
import com.amulet.cavinist.web.data.input.InvalidInputDataException
import org.springframework.dao.DataIntegrityViolationException

object ErrorCodeMapper {

    fun errorCodeForException(exception: Throwable): ErrorCode? = when (exception) {
        is InvalidInputDataException               -> ErrorCode.INVALID_INPUT_DATA
        is AuthenticationFailureException          -> ErrorCode.AUTH_FAILURE
        is InvalidOrMissingAuthenticationException -> ErrorCode.AUTH_FAILURE
        is LoginPolicyFailureException             -> ErrorCode.LOGIN_POLICY_FAILURE
        is PasswordPolicyFailureException          -> ErrorCode.PASSWORD_POLICY_FAILURE
        is ObjectNotFoundException                 -> ErrorCode.OBJECT_NOT_FOUND
        is DataIntegrityViolationException         -> ErrorCode.OBJECT_ALREADY_EXISTS
        is OutdatedVersionException                -> ErrorCode.OUTDATED_VERSION
        else                                       -> null
    }
}