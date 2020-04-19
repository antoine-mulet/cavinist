package com.amulet.cavinist.web.graphql

import com.amulet.cavinist.persistence.repository.OutdatedVersionException
import com.amulet.cavinist.service.ObjectNotFoundException
import com.amulet.cavinist.web.data.input.InvalidInputDataException
import org.springframework.dao.DataIntegrityViolationException

object ErrorCodeMapper {

    fun errorCodeForException(exception: Throwable): ErrorCode? = when (exception) {
        is InvalidInputDataException       -> ErrorCode.INVALID_INPUT_DATA
        is ObjectNotFoundException         -> ErrorCode.OBJECT_NOT_FOUND
        is DataIntegrityViolationException -> ErrorCode.OBJECT_ALREADY_EXISTS
        is OutdatedVersionException        -> ErrorCode.OUTDATED_VERSION
        else                               -> null
    }
}