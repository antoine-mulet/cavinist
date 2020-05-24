package com.amulet.cavinist.web.graphql

enum class ErrorCode {
    INVALID_INPUT_DATA,
    AUTH_FAILURE,
    LOGIN_POLICY_FAILURE,
    PASSWORD_POLICY_FAILURE,
    OBJECT_NOT_FOUND,
    OBJECT_ALREADY_EXISTS,
    OUTDATED_VERSION
}