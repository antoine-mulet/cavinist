package com.amulet.cavinist.web.graphql

data class InvalidOrMissingAuthenticationException(val msg: String): Exception(msg)